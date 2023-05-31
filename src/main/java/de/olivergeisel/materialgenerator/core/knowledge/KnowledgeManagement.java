package de.olivergeisel.materialgenerator.core.knowledge;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.BasicRelation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Service
public class KnowledgeManagement {

	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";

	private static KnowledgeModel knowledge;

	private static KnowledgeModel createKnowledgeModel(Object parsed) {
		KnowledgeModel back = new KnowledgeModel();
		var model = (List<Map<String, ?>>) parsed;
		for (var element : model) {
			var jsonElement = (Map<String, ?>) element;
			var correct = getKnowledgeElement(jsonElement);
			back.addKnowledge(correct);
		}
		return back;
	}

	private static KnowledgeElement getKnowledgeElement(Map<String, ?> jsonElement) {
		var typeString = jsonElement.get("typ").toString();
		var type = KnowledgeType.valueOf(typeString.toUpperCase());
		var content = jsonElement.get("content").toString();
		var id = jsonElement.get("id").toString();
		Collection<Relation> relations;
		if (jsonElement.containsKey("relations")) {
			relations = new LinkedList<>();
			for (var relation : (Collection<Map<String, ?>>) jsonElement.get("relations")) {
				var relationType = (RelationType) relation.get("rel-type");
				var other = relation.get("other").toString();
				relations.add(new BasicRelation(relationType, id, other));
			}
		} else {
			relations = List.of();
		}
		return switch (type) {
			case FACT -> new Fact(content, id, type.name(), relations);
			case DEFINITION -> new Definition(content, id, type.name(), relations);
			case TERM -> new Term(content, id, type.name(), relations);
			case PROOF -> new Proof(content, id, type.name(), relations);
			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
	}

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

	private Set<KnowledgeElement> findRelatedData(String element) {
		return knowledge.findAll(element);

	}

	private Map<String, String> getPlain(String definition) {
		Map<String, String> back = new HashMap<>();
		List<Map<String, ?>> objects = (List<Map<String, ?>>) pareseFromPath(KNOWLEDGE_PATH);
		for (var element : objects) {
			var type = KnowledgeType.valueOf(element.get("typ").toString().toUpperCase());
			if (type == KnowledgeType.DEFINITION && ((String) element.get("id")).contains(definition)) {
				back.put("term", definition.split("-")[0]);
				back.put("definition", (String) element.get("content"));
				break;
			}
		}
		return back;
	}

//region getter / setter
	//
	private static KnowledgeModel getKnowledge() {
		if (knowledge == null) {
			var parsed = pareseFromPath(KNOWLEDGE_PATH);
			knowledge = createKnowledgeModel(parsed);
		}
		return knowledge;
	}

	public List<Map<String, ?>> getNewModelAsJSON() {
		return (List<Map<String, ?>>) pareseFromPath(KNOWLEDGE_PATH);
	}
//endregion
//
}
