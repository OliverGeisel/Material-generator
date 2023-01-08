package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
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
	private static final String[] META_ATTRIBUTES = {"name", "year", "grade", "type", "description"};
	JSONParser parser;

	private final List<ContentTarget> targets = new ArrayList<>();

	private CourseMetadata parseMetadata(Map<String, ?> mapping) {
		String name = (String) mapping.get("name");
		String year = mapping.get("year").toString();
		String level = (String) mapping.get("level");
		String type = (String) mapping.get("type");
		String description = (String) mapping.get("description");
		var computed = mapping.entrySet().stream().filter(x -> Arrays.stream(META_ATTRIBUTES).noneMatch(y -> y.equals(x.getKey())));
		Map<String, String> rest = new HashMap<>();
		computed.forEach(entry -> rest.put(entry.getKey(), entry.getValue().toString()));
		return new CourseMetadata(name, year, level, type, description, rest);

	}

	private StructureChapter createChapter(Map<String, ?> chapterJson) {
		List<Map<String, ?>> groups = (List<Map<String, ?>>) chapterJson.get("groups");
		String name = chapterJson.get("name").toString();
		String weight = chapterJson.get("weight").toString();
		String topicName = chapterJson.get(TOPIC) != null ? chapterJson.get(TOPIC).toString() : "";
		var topic = findTopic(topicName);
		var back = new StructureChapter(topic, Relevance.TO_SET, name, Double.parseDouble(weight));
		for (var group : groups) {
			back.add(createGroup(group));
		}
		List<String> knowledgeAreasJSON = (List<String>) chapterJson.get("knowledgeAreas");
		var knowledgeAreas = crateAreas(knowledgeAreasJSON);
		for (var elem : knowledgeAreas) {
			back.addAlias(elem);
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Chapter %s is not valid", back.getName()));
		}
		return back;
	}

	private List<KnowledgeStructure> crateAreas(List<String> knowledgeAreasJSON) {
		var back = new ArrayList<KnowledgeStructure>();
		for (var elem : knowledgeAreasJSON) {
			back.add(new PotenzialKnowledgeStructure(elem));
		}
		return back;
	}

	private StructureElementPart createGroup(Map<String, ?> groupJSON) {
		String name = groupJSON.get("name").toString();
		String topicName = groupJSON.get(TOPIC) != null ? groupJSON.get(TOPIC).toString() : "";
		var topic = findTopic(topicName);
		var back = new StructureGroup(topic, Relevance.TO_SET, name);
		List<Map<String, ?>> tasks = (List<Map<String, ?>>) groupJSON.get("tasks");
		for (var task : tasks) {
			// Todo decide between group and task!
			back.add(createTask(task));
		}
		List<String> knowledgeAreasJSON = (List<String>) groupJSON.get("knowledgeAreas");
		var knowledgeAreas = crateAreas(knowledgeAreasJSON);
		for (var elem : knowledgeAreas) {
			back.addAlias(elem);
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Group %s is not valid!", back.getName()));
		}
		return back;
	}

	private StructureTask createTask(Map<String, ?> task) {
		Relevance relevance = Relevance.valueOf(task.get("relevance").toString());
		String name = task.get("name").toString();
		String topicName = task.get(TOPIC) != null ? task.get(TOPIC).toString() : "";
		ContentTarget topic = findTopic(topicName);

		var back = new StructureTask(topic, relevance, name);
		List<String> knowledgeAreasJSON = (List<String>) task.get("knowledgeAreas");
		var knowledgeAreas = crateAreas(knowledgeAreasJSON);
		for (var elem : knowledgeAreas) {
			back.addAlias(elem);
		}
		return back;
	}

	private ContentTarget findTopic(String goal) {
		return targets.stream().filter(goalElement -> goalElement.getId().equals(goal)).findFirst().orElse(ContentTarget.EMPTY);
	}

	private CourseStructure parseCourseStructure(List<Map<String, ?>> structure) {
		CourseStructure back = new CourseStructure();
		for (var chapterJson : structure) {
			StructureChapter newChapter = createChapter(chapterJson);
			back.add(newChapter);
		}
		return back;
	}

	private List<ContentGoal> parseCurriculumGoals(Map<String, ?> mapping) {
		List<ContentGoal> back = new LinkedList<>();
		for (var entry : mapping.entrySet()) {
			String goalName = entry.getKey();
			Map<String, ?> goal = (Map<String, ?>) entry.getValue();
			String expression = goal.get("expression").toString().toUpperCase().replace("-", "_");
			String target = goal.get("target").toString();
			String completeSentence = goal.get("completeSentence").toString();
			//List<String> specificWords = goal.get("content") instanceof List<?> list ? (List<String>) list : List.of();
			List<String> contentRaw = goal.get("content") instanceof List<?> list ? (List<String>) list : List.of();
			var content = contentRaw.stream().map(ContentTarget::new).toList();
			back.add(new ContentGoal(ContentGoalExpression.valueOf(expression), target, content, completeSentence, goalName));
			// todo add targets
			targets.addAll(content);
		}
		return back;
	}

	public CoursePlan parseFromFile(InputStream file) throws FileNotFoundException {
		CoursePlan back = null;
		parser = new JSONParser(file);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if (parsedObject instanceof HashMap<?, ?> curriculum) {
			Map<String, ?> metaJSON;
			Map<String, ?> contentJSON;
			Map<String, Map<String, ?>> goalsJSON = new HashMap<>();
			metaJSON = curriculum.get("meta") instanceof HashMap<?, ?> metaJSON_Temp
					? (Map<String, ?>) metaJSON_Temp : null;

			contentJSON = (Map<String, ?>) curriculum.get("content");
			var curriculumGoalsEntries = contentJSON.entrySet()
					.stream().filter(entry -> Pattern.matches("goal-\\d+", entry.getKey()));
			curriculumGoalsEntries.forEach(entry ->
					goalsJSON.put(entry.getKey(), (Map<String, ?>) entry.getValue())
			);
			List<ContentGoal> goals = parseCurriculumGoals(goalsJSON);

			List<Map<String, ?>> courseStructure = (List<Map<String, ?>>) curriculum.get("structure");
			back = new CoursePlan(parseMetadata(metaJSON), goals,
					parseCourseStructure(courseStructure), targets);
		}
		return back;

	}

	public CoursePlan parseFromFile(File file) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(file);
		return parseFromFile(input);
	}
}
