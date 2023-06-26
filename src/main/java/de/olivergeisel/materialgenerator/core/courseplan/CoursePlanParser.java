package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CoursePlanParser {

	private static final String TOPIC = "topic";
	private static final String[] META_ATTRIBUTES = {"name", "year", "level", "type", "description"};

	/**
	 * Parses Metadata of plan.
	 *
	 * @param mapping the mapping of the course plan
	 * @return the parsed Metadata or an empty MetataDataSet if the map is empty
	 */
	private CourseMetadata parseMetadata(Map<String, ?> mapping) {
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

	private final List<ContentTarget> targets = new ArrayList<>();

	private Set<KnowledgeObject> crateAliasObject(List<String> alternativesJSON) {
		var back = new HashSet<KnowledgeObject>();
		for (var elem : alternativesJSON) {
			back.add(new PotenzialKnowledgeObject(elem));
		}
		return back;
	}

	private StructureChapter createChapter(Map<String, ?> chapterJSON) {
		List<Map<String, ?>> groups = (List<Map<String, ?>>) chapterJSON.get("groups");
		String name = chapterJSON.get("name").toString();
		String weight = chapterJSON.get("weight").toString();
		var alternatives = crateAlias(name, (List<String>) chapterJSON.get("alternatives"));
		String topicName = chapterJSON.get(TOPIC) != null ? chapterJSON.get(TOPIC).toString() : "";
		var target = findTopic(topicName);
		var back = new StructureChapter(target, Relevance.TO_SET, name, Double.parseDouble(weight), alternatives);
		for (var group : groups) {
			back.add(createGroup(group));
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Chapter %s is not valid", back.getName()));
		}
		return back;
	}

	private Set<String> crateAlias(String normalName, List<String> alternativesJSON) {
		var back = new HashSet<String>();
		if (alternativesJSON != null) {
			back.add(normalName);
		}
		if (alternativesJSON != null) {
			back.addAll(alternativesJSON);
		}
		return back;
	}

	/**
	 * Gets relevant ContentTarget from the name;
	 *
	 * @param topic the name of the topic
	 * @return the ContentTarget or an empty ContentTarget if no target was found
	 */
	private ContentTarget findTopic(String topic) {
		return targets.stream().filter(goalElement -> goalElement.getTopic().equals(topic)).findFirst().orElse(ContentTarget.EMPTY);
	}

	private StructureElementPart createGroup(Map<String, ?> groupJSON) {
		String name = groupJSON.get("name").toString();
		String topicName = groupJSON.get(TOPIC) != null ? groupJSON.get(TOPIC).toString() : "";
		var topic = findTopic(topicName);
		var alternatives = crateAlias(name, ((List<String>) groupJSON.get("alternatives")));
		var back = new StructureGroup(topic, Relevance.TO_SET, name, alternatives);
		List<Map<String, ?>> tasks = (List<Map<String, ?>>) groupJSON.get("tasks");
		for (var task : tasks) {
			// Todo decide between group and task!
			back.add(createTask(task));
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Group %s is not valid!", back.getName()));
		}
		return back;
	}

	private StructureTask createTask(Map<String, ?> taskJSON) {
		Relevance relevance = Relevance.valueOf(taskJSON.get("relevance").toString());
		String name = taskJSON.get("name").toString();
		String topicName = taskJSON.get(TOPIC) != null ? taskJSON.get(TOPIC).toString() : "";
		ContentTarget topic = findTopic(topicName);
		var alternatives = crateAlias(name, (List<String>) taskJSON.get("alternatives"));
		return new StructureTask(topic, relevance, name, alternatives);
	}

//region getter / setter
	public List<ContentTarget> getTargets() {
		return Collections.unmodifiableList(targets);
	}
//endregion

	private CourseStructure parseCourseStructure(List<Map<String, ?>> structure) {
		CourseStructure back = new CourseStructure();
		for (var chapterJson : structure) {
			StructureChapter newChapter = createChapter(chapterJson);
			back.add(newChapter);
		}
		return back;
	}

	/**
	 * Parses the CurriculumGoals from the given mapping.
	 *
	 * @param mapping the mapping with all goals to parse
	 * @return a list of all parsed goals
	 */
	private List<ContentGoal> parseCurriculumGoals(Map<String, ?> mapping) {
		List<ContentGoal> back = new LinkedList<>();
		for (var entry : mapping.entrySet()) {
			String goalName = entry.getKey();
			Map<String, ?> goalValues = (Map<String, ?>) entry.getValue();
			String expression = goalValues.get("expression").toString().toUpperCase().replace("-", "_");
			String target = goalValues.get("target").toString();
			String completeSentence = goalValues.get("completeSentence").toString();
			List<String> contentRaw = goalValues.get("content") instanceof List<?> list ? (List<String>) list : List.of();
			var content = contentRaw.stream().map(ContentTarget::new).toList();
			back.add(new ContentGoal(ContentGoalExpression.valueOf(expression), target, content, completeSentence, goalName));
			targets.addAll(content);
		}
		return back;
	}

	public CoursePlan parseFromFile(InputStream file) throws FileNotFoundException, RuntimeException {
		CoursePlan back = null;
		var parser = new JSONParser(file);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if (parsedObject instanceof HashMap<?, ?> parsedPlan) {
			Map<String, ?> metaJSON;
			Map<String, ?> contentJSON;
			Map<String, Map<String, ?>> goalsJSON = new HashMap<>();
			// META
			metaJSON = parsedPlan.get("meta") instanceof HashMap<?, ?> metaJSON_Temp
					? (Map<String, ?>) metaJSON_Temp : null;
			var meta = parseMetadata(metaJSON);
			// CONTENT
			contentJSON = (Map<String, ?>) parsedPlan.get("content");
			var curriculumGoalsEntries = contentJSON.entrySet()
					.stream().filter(entry -> Pattern.matches("goal-\\d+", entry.getKey()));
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

	public CoursePlan parseFromFile(File file) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(file);
		return parseFromFile(input);
	}
}
