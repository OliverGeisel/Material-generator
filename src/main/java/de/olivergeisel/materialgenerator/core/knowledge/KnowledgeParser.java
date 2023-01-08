package de.olivergeisel.materialgenerator.core.knowledge;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
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
			List<?> knowledge = (List<?>) knowledgeModel.get("knowledge");
			List<?> source = (List<?>) knowledgeModel.get("source");

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

	private KnowledgeElement parseKnowledge(List<?> knowledgeJSON) {
		return null;
	}

	private Set<KnowledgeSource> parseSource(List<?> sourceJSON) {
		Set<KnowledgeSource> back = new HashSet<>();
		return back;
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
