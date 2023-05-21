package de.olivergeisel.materialgenerator.generation.template;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneratorService {
	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";
	private static KnowledgeModel knowledge;

	private static Object parseFromInputStream(InputStream input) {
		var parser = new JSONParser(input);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return parsedObject;
	}

	private static Object parseFile(File file) {
		InputStream input;
		try {
			input = new FileInputStream(file);
			return parseFromInputStream(input);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object pareseFromPath(String path) {
		File file = new File(path);
		return parseFile(file);
	}

	private void createKnowledgeModel(Object parsed) {
		knowledge = new KnowledgeModel();
		var model = (List<Map<String, ?>>) parsed;

		for (var element : model) {
			var jsonElement = (Map<String, ?>) element;
			var correct = getKnowledgeElement(jsonElement);
			knowledge.add(correct);
		}
	}

	private KnowledgeElement getKnowledgeElement(Map<String, ?> jsonElement) {
		// TODO: 2023-5-21 fixing this
		var type = jsonElement.get("typ").toString();
		var content = jsonElement.get("content").toString();
		var id = jsonElement.get("id").toString();
		String structure = jsonElement.get("structure").toString();
		var relationsStrings = (List<String>) jsonElement.get("relations");
		//var relations = relationsStrings.stream().map(Relation::new).toList();
		return null;
		//ElementGenerator.create(type, id, content, relations);
	}

	public Map<String, String> getPlain(String definition) {
		Map<String, String> back = new HashMap<>();
		List<Map<String, ?>> objects = (List<Map<String, ?>>) pareseFromPath(KNOWLEDGE_PATH);
		for (var element : objects) {
			if (element.get("typ").equals("Definition") && ((String) element.get("id")).contains(definition)) {
				back.put("term", definition.split("-")[0]);
				back.put("definition", (String) element.get("content"));
				break;
			}
		}
		return back;
	}

	//
//
	public KnowledgeModel getKnowledge() {
		if (knowledge == null) {
			var parsed = pareseFromPath(KNOWLEDGE_PATH);
			createKnowledgeModel(parsed);
		}
		return knowledge;
	}
}
