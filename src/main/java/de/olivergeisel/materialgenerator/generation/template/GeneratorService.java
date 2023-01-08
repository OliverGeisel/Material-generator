package de.olivergeisel.materialgenerator.generation.template;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.*;
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

	private static final String DEFINITION = "DEFINITION";
	private static final String TERM = "TERM";
	private static final String FACT = "FACT";
	private static final String ACRONYM = "ACRONYM";
	private static final String PROOF = "PROOF";
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
		var type = jsonElement.get("typ").toString();
		var content = jsonElement.get("content").toString();
		var id = jsonElement.get("id").toString();
		var relations = (List<String>) jsonElement.get("relations");
		return switch (type.toUpperCase()) {
			case FACT -> new Fact(content, id, type, relations);
			case ACRONYM -> new Acronym(content, id, type, relations);
			case DEFINITION -> new Definition(content, id, type, relations);
			case TERM -> new Term(content, id, type, relations);
			case PROOF -> new Proof(content, id, type, relations);
			default -> throw new IllegalStateException("Unexpected KnowledgeElement: " + type);
		};
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
	public KnowledgeModel getKnowledge() {
		if (knowledge == null) {
			var parsed = pareseFromPath(KNOWLEDGE_PATH);
			createKnowledgeModel(parsed);
		}
		return knowledge;
	}
//
}
