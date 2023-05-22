package de.olivergeisel.materialgenerator.core.knowledge;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.ElementGenerator;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationGenerator;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.*;
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
			List<Map<String, ?>> source = (List<Map<String, ?>>) knowledgeModel.get("sources");

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

	// ------------------ Knowledge ------------------
	private Set<KnowledgeElement> parseKnowledge(List<Map<String, ?>> knowledgeJSON) {
		if (knowledgeJSON.isEmpty()) {
			return new HashSet<>();
		}
		Set<KnowledgeElement> back = new HashSet<>();
		for (Map<String, ?> element : knowledgeJSON) {
			back.add(getKnowledgeElement(element));
		}
		return back;

	}

	private KnowledgeElement getKnowledgeElement(Map<String, ?> jsonElement) {
		var type = jsonElement.get("typ").toString();
		var content = jsonElement.get("content").toString();
		var id = jsonElement.get("id").toString();
		String structure = jsonElement.get("structure").toString();
		var relationsJSON = (List<Map<String, String>>) jsonElement.get("relations");
		var relations = createRelation(relationsJSON);
		return ElementGenerator.create(type, id, structure, content, relations);
	}

	private Set<Relation> createRelation(List<Map<String, String>> relationsJSON) {
		var back = new HashSet<Relation>();
		for (var relation : relationsJSON) {
			var newRelation = RelationGenerator.create(relation.get("relation-type"), relation.get("relation-id"));
			back.add(newRelation);
		}
		return back;
	}


	// ------------------ Source ------------------
	private Set<KnowledgeSource> parseSource(List<Map<String, ?>> sourceJSON) {
		Set<KnowledgeSource> back = new HashSet<>();
		if (sourceJSON.isEmpty()) {
			return back;
		}
		for (var source : sourceJSON) {
			String type = source.get("type").toString();
			String id = source.get("id").toString();
			String name = source.get("name").toString();
			String content = source.get("content").toString(); // Todo
			back.add(switch (type) {
						case "INTERNALMEDIA" -> new InternalMedia(id, name);
						case "UNKNOWNSOURCE" -> UnknownSource.getInstance();
						case "NOTRESOLVABLEREFERENCE" -> new NotResolvableReference(id, name);
						case "RESOLVABLEREFERENCE" -> new ResolvableReference(id, name);
						default -> throw new IllegalArgumentException("Unknown Source-type");
					}
			);
		}
		return back;
	}

	// ------------------ Structure ------------------
	private KnowledgeStructure parseStructure(Map<String, ?> structureJSON) {
		var back = new KnowledgeStructure();
		var root = back.getRoot();
		root.setKey("_root");
		root.setName(structureJSON.get("name").toString());
		// check if empty
		if (structureJSON.isEmpty()) {
			return back;
		}
		var parts = (List<Map<String, ?>>) structureJSON.get("children");
		for (var child : parts) {
			root.addObject(parseKnowledgeObject(child));
		}
		return back;
	}

	/**
	 * Parse a part of the structure and its children.
	 *
	 * @param part the part to parse.
	 * @return the parsed part as KnowledgeObject or null if part is empty.
	 */
	private KnowledgeObject parseKnowledgeObject(Map<String, ?> part) {
		KnowledgeObject back;
		// check if empty
		if (part.isEmpty()) {
			return null;
		}
		var id = part.get("id").toString();
		var children = (List<Map<String, ?>>) part.get("children");
		// check if parts is empty
		if (children.isEmpty()) {
			back = new KnowledgeLeaf(id);
		} else {
			var newFragment = new KnowledgeFragment(id);
			for (var childPart : children) {
				newFragment.addObject(parseKnowledgeObject(childPart));
			}
			back = newFragment;
		}
		return back;
	}


}
