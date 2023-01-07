package de.olivergeisel.materialgenerator.test;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;

import java.io.File;
import java.io.FileNotFoundException;

public class TestRuns {

	public static void main(String[] args){
		CoursePlanParser parser = new CoursePlanParser();
		File jsonFile = new File("src/main/resources/data/curriculum/Test-Plan.json");
		if (jsonFile.canRead()) {
			try {
				parser.parseFromFile(jsonFile);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
