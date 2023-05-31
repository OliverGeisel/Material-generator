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

	public static final String TYPE = "type";
	public static final String ID = "id";
	public static final String STRUCTURE = "structure";
	public static final String CONTENT = "content";
	public static final String RELATIONS = "relations";
	public static final String RELATION_TYPE = "relation_type";
	public static final String RELATION_ID = "relation_id";
	// Source fields
	public static final String NAME = "name";

	// Structure fields
	public static final String CHILDREN = "children";
	Logger logger = LoggerFactory.getLogger(KnowledgeParser.class);

	//region Source parsing
	private KnowledgeElement createElement(String type, String id, String structure, String content,
										   List<Map<String, String>> relationsJSON) {
		var newElement = ElementGenerator.create(type, id, structure, content);
		var relations = createRelation(relationsJSON, newElement);
		newElement.addRelations(relations);
		return newElement;
	}

	private Set<Relation> createRelation(List<Map<String, String>> relationsJSON, KnowledgeElement fromElement) {
		var back = new HashSet<Relation>();
		for (var relation : relationsJSON) {
			var newRelation = RelationGenerator.create(relation.get(RELATION_TYPE), fromElement.getId(), relation.get(RELATION_ID));
			back.add(newRelation);
		}
		return back;
	}

	public KnowledgeModel parseFromFile(File jsonFile) throws FileNotFoundException {
		FileInputStream input = new FileInputStream(jsonFile);
		return parseFromFile(input);
	}

	public KnowledgeModel parseFromFile(InputStream file) {
		KnowledgeModel back;
		JSONParser parser = new JSONParser(file);
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
			back = new KnowledgeModel(parsedStructure.getRoot());
			back.addSource(parsedSource);
			back.addKnowledge(parsedKnowledge);
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
			String type = element.get(TYPE).toString();
			String id = element.get(ID).toString();
			String structure = element.get(STRUCTURE).toString();
			String content = element.get(CONTENT).toString();
			List<Map<String, String>> relationsJSON = (List<Map<String, String>>) element.get(RELATIONS);
			back.add(createElement(type, id, structure, content, relationsJSON));
		}
		return back;
	}
	//endregion

	private KnowledgeObject parseKnowledgeObject(Map<String, ?> partJSON) throws IncompleteJSONException {
		KnowledgeObject back;
		// check if empty
		if (partJSON.isEmpty()) {
			throw new IncompleteJSONException("KnowledgeObject is empty!");
		}
		var id = partJSON.get(ID).toString();
		var parts = (List<Map<String, ?>>) partJSON.get(CHILDREN);
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
	//endregion

	//region Source Parsing
	private Set<KnowledgeSource> parseSource(List<Map<String, ?>> sourceJSON) {
		Set<KnowledgeSource> back = new HashSet<>();
		if (sourceJSON.isEmpty()) {
			return back;
		}
		for (var source : sourceJSON) {
			String type = source.get(TYPE).toString();
			String id = source.get(ID).toString();
			String name = source.get(NAME).toString();
			String content = source.get(CONTENT).toString(); // Todo
			back.add(switch (SourceType.valueOf(type.toUpperCase())) {
						case INTERNAL_MEDIA -> new InternalMedia(id, name);
						case UNKNOWN_SOURCE -> UnknownSource.getInstance();
						case NOT_RESOLVABLE_REFERENCE -> new NotResolvableReference(id, name);
						case RESOLVABLE_REFERENCE -> new ResolvableReference(id, name);
					}
			);
		}
		return back;
	}

	//region Structure Parsing
	private KnowledgeStructure parseStructure(Map<String, ?> structureJSON) {
		var back = new KnowledgeStructure();
		var root = back.getRoot();
		// check if empty
		if (structureJSON.isEmpty()) {
			return back;
		}
		var parts = (List<Map<String, ?>>) structureJSON.get(CHILDREN);
		for (var part : parts) {
			try {
				root.addObject(parseKnowledgeObject(part));
			} catch (IncompleteJSONException e) {
				logger.warn(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return back;
	}
	//endregion
}
