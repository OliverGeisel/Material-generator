package de.olivergeisel.materialgenerator.test;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;
import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeParser;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;

import java.io.File;
import java.io.FileNotFoundException;

public class TestRuns {

	public static void testKnowledgeModel() {
		KnowledgeParser parser = new KnowledgeParser();
		File jsonFile = new File("src/main/resources/data/knowledge/Test-Knowledge.json");
		if (jsonFile.canRead()) {
			try {
				KnowledgeModel knowledgeModel = parser.parseFromFile(jsonFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void testCoursePlan() {
		CoursePlanParser parser = new CoursePlanParser();
		File jsonFile = new File("src/main/resources/data/curriculum/Test-Plan.json");
		if (jsonFile.canRead()) {
			try {
				CoursePlan plan = parser.parseFromFile(jsonFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			testCoursePlan();
			return;
		}
		switch (TestSelect.valueOf(args[0].toUpperCase().replace("-", "_"))) {
			case COURSE_PLAN -> testCoursePlan();
			case KNOWLEDGE_MODEL -> testKnowledgeModel();
			default -> testCoursePlan();
		}
	}

	private enum TestSelect {
		COURSE_PLAN,
		KNOWLEDGE_MODEL
	}
}


