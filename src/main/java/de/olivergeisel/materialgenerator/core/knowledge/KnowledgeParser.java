package de.olivergeisel.materialgenerator.core.knowledge;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations.BasicRelation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations.CustomRelation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations.RelationType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeLeaf;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnowledgeParser {

	private static final String TERM = "TERM";
	private static final String DEFINITION = "DEFINITION";
	private static final String FACT = "FACT";
	private static final String PROOF = "PROOF";
	private static final String EXERCISE = "EXERCISE";
	private static final String EXAMPLE = "EXAMPLE";
	private static final String EXPLANATION = "EXPLANATION";
	private static final String NODE = "NODE";
	Logger logger = LoggerFactory.getLogger(KnowledgeParser.class);

	JSONParser parser;


	public KnowledgeModel parseFromFile(File jsonFile) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(jsonFile);
		return parseFromFile(input);
	}

	public KnowledgeModel parseFromFile(InputStream file) {
		KnowledgeModel back = null;
		parser = new JSONParser(file);
		Object parsedObject;
		try {
			parsedObject = parser.parse();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if (parsedObject instanceof Map<?, ?> knowledgeModel) {
			Map<String, ?> structure = (Map<String, ?>) knowledgeModel.get("structure");
			List<Map<String, ?>> knowledge = (List<Map<String, ?>>) knowledgeModel.get("knowledge");
			Map<?, ?> source = (Map<?, ?>) knowledgeModel.get("sources");

			var parsedStructure = parseStructure(structure);
			var parsedSource = parseSource(source);
			var parsedKnowledge = parseKnowledge(knowledge);
			back = new KnowledgeModel();
			back.addStructure(parsedStructure);
			back.addSource(parsedSource);
			back.add(parsedKnowledge);
		} else {
			logger.warn("No valid knowledge-Fiel! Create empty KnowledgeModel");
			back = new KnowledgeModel();
		}
		return back;
	}

	private Set<KnowledgeElement> parseKnowledge(List<Map<String, ?>> knowledgeJSON) {
		if (knowledgeJSON.isEmpty()) {
			return new HashSet<>();
		}
		Set<KnowledgeElement> back = new HashSet<>();
		for (Map<String, ?> element : knowledgeJSON) {
			String type = element.get("type").toString();
			String id = element.get("id").toString();
			String structure = element.get("structure").toString();
			String content = element.get("content").toString();
			List<Map<String, String>> relations = (List<Map<String, String>>) element.get("relations");
			back.add(createElement(type, id, structure, content, relations));
		}
		return back;

	}

	private KnowledgeElement getKnowledgeElement(Map<String, ?> jsonElement) {
		var type = jsonElement.get("typ").toString();
		var content = jsonElement.get("content").toString();
		var id = jsonElement.get("id").toString();
		var relationsJSON = (List<Map<String, String>>) jsonElement.get("relations");
		var relations = createRelation(relationsJSON);
		return switch (type.toUpperCase()) {
			case TERM -> new Term(content, id, type, relations);
			case DEFINITION -> new Definition(content, id, type, relations);
			case FACT -> new Fact(content, id, type, relations);
			case PROOF -> new Proof(content, id, type, relations);
			case EXERCISE -> new Exercise(content, id, type, relations);
			case EXPLANATION -> new Explanation(content, id, type, relations);
			case "STATEMENT" -> new Statement(content, id, type, relations);
			case EXAMPLE -> new Example(content, id, type, relations);
			default -> throw new IllegalStateException("Unexpected KnowledgeElement: " + type);
		};
	}

	private Set<Relation> createRelation(List<Map<String, String>> relationsJSON) {
		var back = new HashSet<Relation>();
		for (var relation : relationsJSON) {
			var id = relation.get("relation-id");
			RelationType type;
			try {
				type = RelationType.valueOf(relation.get("relation-type"));
			} catch (IllegalArgumentException iae) {
				type = RelationType.CUSTOM;
			}
			Relation newRelation;
			if (type != RelationType.CUSTOM) {

				newRelation = new BasicRelation(type);
			} else {
				newRelation = new CustomRelation(id, type);
			}
			back.add(newRelation);
		}
		return back;
	}

	private KnowledgeElement createElement(String type, String id, String structure, String content, List<Map<String, String>> relationsJSON) {
		KnowlegeType kType = KnowlegeType.valueOf(type.toUpperCase());
		var relations = createRelation(relationsJSON);
		return switch (kType) {
			case FACT -> new Fact(content, id, type, relations);
			case DEFINITION -> new Definition(content, id, type, relations);
			case TERM -> new Term(content, id, type, relations);
			case PROOF -> new Proof(content, id, type, relations);
			default -> throw new IllegalStateException("Unexpected KnowledgeElement: " + type);
		};

	}

	private Set<KnowledgeSource> parseSource(Map<?, ?> sourceJSON) {
		Set<KnowledgeSource> back = new HashSet<>();
		if (sourceJSON.isEmpty()) {
			return back;
		}
		return null;
	}

	private KnowledgeStructure parseStructure(Map<String, ?> structureJSON) {
		var back = new KnowledgeStructure();
		var root = back.getRoot();
		// check if empty
		if (structureJSON.isEmpty()) {
			return back;
		}
		var parts = (List<Map<String, ?>>) structureJSON.get("parts");
		for (var part : parts) {
			// check if empty
			KnowledgeObject newPart;
			var id = part.get("id").toString();
			if (((List<?>) part.get("parts")).isEmpty()) {
				newPart = new KnowledgeLeaf(id);
			} else {
				var newFragment = new KnowledgeFragment(id);
				for (var childParts : (List<Map<String, ?>>) part.get("parts")) {
					newFragment.addObject(parseKnowledgeObject(childParts));
				}
				newPart = newFragment;
			}
			root.addObject(newPart);
		}
		return back;
	}

	private KnowledgeObject parseKnowledgeObject(Map<String, ?> part) {
		KnowledgeObject back;
		// check if empty
		if (part.isEmpty()) {
			return null;
		}
		var id = part.get("id").toString();
		var parts = (List<Map<String, ?>>) part.get("parts");
		// check if parts is empty
		if (parts.isEmpty()) {
			back = new KnowledgeLeaf(id);
		} else {
			var newFragment = new KnowledgeFragment(id);
			for (var childParts : parts) {
				newFragment.addObject(parseKnowledgeObject(childParts));
			}
			back = newFragment;
		}
		return back;
	}


}
