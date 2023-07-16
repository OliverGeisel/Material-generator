package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CoursePlanParser {

	private static final String              TOPIC           = "topic";
	private static final String[]            META_ATTRIBUTES = {"name", "year", "level", "type", "description"};
	private static final String              TARGET          = "target";
	private static final String              ALTERNATIVES    = "alternatives";
	private static final String              GROUPS          = "groups";
	private final        List<ContentTarget> targets         = new ArrayList<>();

	private final Logger logger = LoggerFactory.getLogger(CoursePlanParser.class);

	/**
	 * Parses Metadata of plan.
	 *
	 * @param mapping the mapping of the course plan
	 * @return the parsed Metadata or an empty MetataDataSet if the map is empty
	 */
	private CourseMetadata parseMetadata (Map<String, ?> mapping) {
		if (mapping == null) {
			return CourseMetadata.emptyMetadata();
		}
		String name = mapping.get("name").toString();
		String year = mapping.get("year").toString();
		String level = mapping.get("level").toString();
		String type = mapping.get("type").toString();
		String description = (String) mapping.get("description");
		var extraList = (List<Map<String, String>>) mapping.get("extra");
		var extra = new HashMap<String, String>();
		extraList.forEach(elem -> extra.put(elem.get("name"), elem.get("value")));
		return new CourseMetadata(name, year, level, type, description, extra);

	}

	private Set<KnowledgeObject> crateAliasObject (List<String> alternativesJSON) {
		var back = new HashSet<KnowledgeObject>();
		for (var elem : alternativesJSON) {
			back.add(new PotenzialKnowledgeObject(elem));
		}
		return back;
	}

	/**
	 * Creates an ordered set of aliases from a normal name and a list of alternatives.
	 *
	 * @param normalName       most important alias
	 * @param alternativesJSON list of alternatives
	 * @return ordered set of aliases
	 * @throws IllegalArgumentException if the normal name is null or empty
	 */
	private Set<String> crateAlias (String normalName, List<String> alternativesJSON) throws IllegalArgumentException {
		var back = new LinkedHashSet<String>();
		if (normalName == null || normalName.isBlank()) {
			throw new IllegalArgumentException("Normal name must not be null or blank");
		}
		back.add(normalName);
		if (alternativesJSON != null) {
			for (var elem : alternativesJSON) {
				if (elem != null && !elem.isBlank()) {
					back.add(elem);
				}
			}
		}
		return back;
	}

	/**
	 * Parses a chapter plan from a parsed JSON.
	 *
	 * @param chapterJSON the parsed JSON
	 * @return the parsed chapter plan
	 * @throws CoursePlanParserException if the JSON is not valid. Like the name is null or empty
	 * @throws IllegalStateException     if the parsed chapter is not valid
	 */
	private StructureChapter createChapter (Map<String, ?> chapterJSON)
			throws CoursePlanParserException, IllegalStateException {
		List<Map<String, ?>> groups = (List<Map<String, ?>>) chapterJSON.get(GROUPS);
		String name = chapterJSON.get("name").toString();
		if (name.isBlank()) {
			throw new CoursePlanParserException("Chapter name must not be null or blank");
		}
		String weight = chapterJSON.get("weight").toString();
		var alternatives = crateAlias(name, (List<String>) chapterJSON.get(ALTERNATIVES));
		String topicName = chapterJSON.get(TOPIC) != null ? chapterJSON.get(TOPIC).toString() : "";
		var target = findTopic(topicName);
		var back = new StructureChapter(target, name, Double.parseDouble(weight), alternatives);
		for (var group : groups) {
			try {
				back.add(createGroup(group));
			} catch (CoursePlanParserException e) {
				logger.error("Error while parsing group in chapter %s".formatted(name), e);
			}
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Chapter %s is not valid", back.getName()));
		}
		return back;
	}

	/**
	 * Gets relevant ContentTarget from the name;
	 *
	 * @param topic the name of the topic
	 * @return the ContentTarget or an empty ContentTarget if no target was found
	 */
	private ContentTarget findTopic (String topic) {
		return targets.stream().filter(goalElement -> goalElement.getTopic().equals(topic)).findFirst()
					  .orElse(ContentTarget.EMPTY);
	}

	private StructureElementPart createGroup (Map<String, ?> groupJSON) throws CoursePlanParserException {
		String name = groupJSON.get("name").toString();
		if (name.isBlank()) {
			throw new CoursePlanParserException("Group name must not be null or blank");
		}
		String topicName = groupJSON.get(TOPIC) != null ? groupJSON.get(TOPIC).toString() : "";
		var topic = findTopic(topicName);
		var alternatives = crateAlias(name, ((List<String>) groupJSON.get(ALTERNATIVES)));
		var back = new StructureGroup(topic, Relevance.TO_SET, name, alternatives);
		List<Map<String, ?>> tasks = (List<Map<String, ?>>) groupJSON.get("tasks");
		for (var task : tasks) {
			// Todo decide between group and task!
			try {
				back.add(createTask(task));
			} catch (CoursePlanParserException e) {
				logger.error(String.format("Error while parsing task in group %s. Skip task and continue", name), e);
			}
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Group %s is not valid!", back.getName()));
		}
		return back;
	}

	private StructureTask createTask (Map<String, ?> taskJSON) throws CoursePlanParserException {
		Relevance relevance = Relevance.valueOf(taskJSON.get("relevance").toString());
		String name = taskJSON.get("name").toString();
		if (name.isBlank()) {
			throw new CoursePlanParserException("Name of task must not be null or blank");
		}
		String topicName = taskJSON.get(TOPIC) != null ? taskJSON.get(TOPIC).toString() : "";
		ContentTarget topic = findTopic(topicName);
		Set<String> alternatives;
		try {
			alternatives = crateAlias(name, (List<String>) taskJSON.get(ALTERNATIVES));
		} catch (IllegalArgumentException e) {
			logger.error("Could not parse task", e);
			throw new CoursePlanParserException(String.format("Could not parse task %s", name), e);
		}
		return new StructureTask(topic, relevance, name, alternatives);
	}

	private CourseStructure parseCourseStructure (List<Map<String, ?>> structure) {
		CourseStructure back = new CourseStructure();
		for (var chapterJson : structure) {
			try {
				StructureChapter newChapter = createChapter(chapterJson);
				back.add(newChapter);
			} catch (IllegalArgumentException e) {
				logger.error("Could not parse chapter", e);
			}
		}
		return back;
	}
//endregion

	/**
	 * Parses the CurriculumGoals from the given mapping.
	 *
	 * @param mapping the mapping with all goals to parse
	 * @return a list of all parsed goals
	 */
	private List<ContentGoal> parseCurriculumGoals (Map<String, ?> mapping) {
		List<ContentGoal> back = new LinkedList<>();
		for (var entry : mapping.entrySet()) {
			String goalName = entry.getKey();
			Map<String, ?> goalValues = (Map<String, ?>) entry.getValue();
			String expression = goalValues.get("expression").toString().toUpperCase().replace("-", "_");
			String target = goalValues.get(TARGET).toString();
			String completeSentence = goalValues.get("completeSentence").toString();
			List<String> contentRaw =
					goalValues.get("content") instanceof List<?> list ? (List<String>) list : List.of();
			var content = contentRaw.stream().map(ContentTarget::new).toList();
			back.add(new ContentGoal(ContentGoalExpression.valueOf(expression), target, content, completeSentence,
					goalName));
			targets.addAll(content);
		}
		return back;
	}

	public CoursePlan parseFromFile (InputStream file) throws CoursePlanParserException {
		CoursePlan back = null;
		var parser = new JSONParser(file);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			logger.error("Could not parse JSON", e);
			throw new CoursePlanParserException("Could not parse JSON", e);
		}
		if (parsedObject instanceof HashMap<?, ?> parsedPlan) {
			Map<String, ?> metaJSON;
			Map<String, ?> contentJSON;
			Map<String, Map<String, ?>> goalsJSON = new HashMap<>();
			// META
			metaJSON = parsedPlan.get("meta") instanceof HashMap<?, ?> metaJsonTemp
					? (Map<String, ?>) metaJsonTemp : null;
			var meta = parseMetadata(metaJSON);
			// CONTENT
			contentJSON = (Map<String, ?>) parsedPlan.get("content");
			var curriculumGoalsEntries = contentJSON.entrySet()
													.stream()
													.filter(entry -> Pattern.matches("goal-\\d+", entry.getKey()));
			curriculumGoalsEntries.forEach(entry ->
					goalsJSON.put(entry.getKey(), (Map<String, ?>) entry.getValue())
			);
			List<ContentGoal> goals = parseCurriculumGoals(goalsJSON);
			// STRUCTURE
			List<Map<String, ?>> courseStructure = (List<Map<String, ?>>) parsedPlan.get("structure");
			back = new CoursePlan(meta, goals, parseCourseStructure(courseStructure), targets);
		}
		return back;

	}

	public CoursePlan parseFromFile (File file) throws FileNotFoundException, CoursePlanParserException {
		FileInputStream input = new FileInputStream(file);
		return parseFromFile(input);
	}

	//region setter/getter
	public List<ContentTarget> getTargets () {
		return Collections.unmodifiableList(targets);
	}
//endregion
}
