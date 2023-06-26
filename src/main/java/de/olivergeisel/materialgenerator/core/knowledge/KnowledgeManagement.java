package de.olivergeisel.materialgenerator.core.knowledge;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.BasicRelation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Service
public class KnowledgeManagement {

	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";

	private final KnowledgeModel knowledge;

	public KnowledgeManagement() {
		knowledge = parseFromPath(KNOWLEDGE_PATH);
	}

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

	private static KnowledgeModel parseFromInputStream(InputStream input) {
		KnowledgeParser parser = new KnowledgeParser();
		try {
			return parser.parseFromFile(input);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	private static KnowledgeModel parseFromFile(File file) {
		KnowledgeParser parser = new KnowledgeParser();
		try {
			return parser.parseFromFile(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static KnowledgeModel parseFromPath(String path) {
		File file = new File(path);
		return parseFromFile(file);
	}

	//region getter / setter

//region setter/getter
	public KnowledgeModel getKnowledge() {
		return knowledge;
	}
//endregion

	public Set<KnowledgeElement> findRelatedData(String elementId) {
		return getKnowledge().findAll(elementId);
	}

//endregion

}
