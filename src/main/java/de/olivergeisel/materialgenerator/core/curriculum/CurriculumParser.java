package de.olivergeisel.materialgenerator.core.curriculum;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CurriculumParser {

	JSONParser parser ;
	private Object meta;

	private static List<CurriculumGoal> parseCurriculumGoal(){
		return null;
	}

	private static MetadataCurriculum parseMetadata(HashMap<String,?> mapping){
		//List<> data;
		return null ;// new MetadataCurriculum();
	}

	public Curriculum parseFromFile(File file) throws FileNotFoundException {
		InputStream input = new FileInputStream(file);

		parser = new JSONParser(input);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if(parsedObject instanceof HashMap<?,?> curriculum){
			HashMap<String,?> meta,rules,innerCurriculum,curriculumGoal,courseStructure;
			meta = (HashMap<String, ?>) curriculum.get("meta");
			rules = (HashMap<String, ?>) curriculum.get("rules");
			innerCurriculum = (HashMap<String, ?>) curriculum.get("curriculum");
			curriculumGoal = (HashMap<String, ?>) curriculum.get("curriculum-goal");
			courseStructure = (HashMap<String, ?>) curriculum.get("curse-structure");
		}

		return null;
	}
}
