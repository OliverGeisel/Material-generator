package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CoursePlanParser {

	private static final String[] META_ATTRIBUTES = {"name", "year", "grade", "type", "description"};
	JSONParser parser;

	private CourseMetadata parseMetadata(HashMap<String, ?> mapping) {
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

	private StructureChapter createChapter(Map<String, ?> chapterJson, List<CurriculumGoal> goals) {
		List<Map<String, ?>> groups = (List<Map<String, ?>>) chapterJson.get("groups");
		String name = chapterJson.get("name").toString();
		String weight = chapterJson.get("weight").toString();
		List<Map<String, ?>> knowledgeAreas = (List<Map<String, ?>>) chapterJson.get("knowledgeAreas");
		var back = new StructureChapter(Relevance.TO_SET, name, Double.parseDouble(weight));
		for (var group : groups) {
			back.add(createGroup(group, goals));
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Chapter %s is not valid", back.getName()));
		}
		return back;
	}

	private StructureElementPart createGroup(Map<String, ?> groupJSON, List<CurriculumGoal> goals) {
		String name = groupJSON.get("name").toString();
		var back = new StructureGroup(Relevance.TO_SET, name);
		List<Map<String, ?>> tasks = (List<Map<String, ?>>) groupJSON.get("tasks");
		for (var task : tasks) {
			// Todo decide between group and task!
			back.add(createTask(task, goals));
		}
		back.updateRelevance();
		if (!back.isValid()) {
			throw new IllegalStateException(String.format("Group %s is not valid!", back.getName()));
		}
		return back;
	}

	private StructureTask createTask(Map<String, ?> task, List<CurriculumGoal> goals) {
		Relevance relevance = Relevance.valueOf(task.get("relevance").toString());
		String name = task.get("name").toString();
		String goalName = task.get("topic").toString();
		CurriculumGoal goal = findGoal(goals, goalName);
		return new StructureTask(goal, relevance, name);
	}

	private CurriculumGoal findGoal(List<CurriculumGoal> goals, String goal) {
		return goals.stream().filter(goalElement -> goalElement.getId().equals(goal)).findFirst().orElse(new CurriculumGoal(CurriculumGoalExpression.CONTROL, "", List.of(), ""));
	}

	private CourseStructure parseCourseStructure(List<Map<String, ?>> structure, List<CurriculumGoal> goals) {
		CourseStructure back = new CourseStructure();
		for (var chapterJson : structure) {
			StructureChapter newChapter = createChapter(chapterJson, goals);
			back.add(newChapter);
		}
		return back;
	}

	private List<CurriculumGoal> parseCurriculumGoals(Map<String, ?> mapping) {
		List<CurriculumGoal> back = new LinkedList<>();
		for (var entry : mapping.entrySet()) {
			String goalName = entry.getKey();
			Map<String, ?> goal = (Map<String, ?>) entry.getValue();
			String expression = goal.get("expression").toString().toUpperCase().replace("-", "_");
			String target = goal.get("target").toString();
			String completeSentence = goal.get("completeSentence").toString();
			List<String> specificWords = goal.get("content") instanceof List<?> list ? (List<String>) list : List.of();
			back.add(new CurriculumGoal(CurriculumGoalExpression.valueOf(expression), target, specificWords, completeSentence, goalName));
		}
		return back;
	}

	public CoursePlan parseFromFile(File file) throws FileNotFoundException {
		InputStream input = new FileInputStream(file);
		CoursePlan back = null;
		parser = new JSONParser(input);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if (parsedObject instanceof HashMap<?, ?> curriculum) {
			HashMap<String, ?> metaJSON;
			HashMap<String, ?> contentJSON;
			Map<String, Map<String, ?>> goalsJSON = new HashMap<>();
			metaJSON = curriculum.get("meta") instanceof HashMap<?, ?> metaJSON_Temp
					? (HashMap<String, ?>) metaJSON_Temp : null;

			contentJSON = (HashMap<String, ?>) curriculum.get("content");
			var curriculumGoalsEntries = contentJSON.entrySet()
					.stream().filter(entry -> Pattern.matches("goal-\\d+", entry.getKey()));
			curriculumGoalsEntries.forEach(entry ->
					goalsJSON.put(entry.getKey(), (Map<String, ?>) entry.getValue())
			);
			List<CurriculumGoal> goals = parseCurriculumGoals(goalsJSON);

			List<Map<String, ?>> courseStructure = (List<Map<String, ?>>) curriculum.get("structure");
			back = new CoursePlan(parseMetadata(metaJSON), goals,
					parseCourseStructure(courseStructure, goals), parseCurriculum(curriculum));
		}
		return back;
	}

	private Curriculum parseCurriculum(HashMap<?, ?> curriculum) {
		return new Curriculum();
	}
}
