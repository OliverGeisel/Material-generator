package de.olivergeisel.materialgenerator.generation.template;


import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GeneratorController {
	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";

	private static final String DEFINITION = "Definition";
	private static final String TERM = "Term";
	private static final String FACT = "Fact";
	private static final String SYNONYM = "Synonym";
	private static final String ACRONYM = "Acronym";
	private static final String PROOF = "Proof";
	private static KnowledgeModel knowledge;

	@Autowired
	public GeneratorController(StorageService storageService) {
		this.storageService = storageService;
	}

	private static KnowledgeModel createKnowlegeModel(Object parsed) {
		KnowledgeModel back = new KnowledgeModel();
		var model = (List<Map<String, ?>>) parsed;

		for (var element : model) {
			var jsonElement = (Map<String, ?>) element;
			var correct = getKnowledgeElement(jsonElement);
			back.add(correct);
		}
		return back;
	}

	private static KnowledgeElement getKnowledgeElement(Map<String, ?> jsonElement) {
		var type = (String) jsonElement.get("typ");
		var content = (String) jsonElement.get("content");
		var id = (String) jsonElement.get("id");
		var relations = (Collection<String>) jsonElement.get("relations");
		KnowledgeElement back = switch (type) {
			case FACT -> new Fact(content,id,type,relations);
			case ACRONYM -> new Acronym(content,id,type,relations);
			case DEFINITION -> new Definition(content,id,type,relations);
			case TERM -> new Term(content,id,type,relations);
			case PROOF -> new Proof(content,id,type,relations);
			case SYNONYM -> new Synonym(content,id,type,relations);
			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
		return back;
	}

	private static KnowledgeModel getKnowledge() {
		if (knowledge == null) {
			var parsed = pareseFromPath(KNOWLEDGE_PATH);
			knowledge = createKnowlegeModel(parsed);
		}
		return knowledge;
	}

	private final StorageService storageService;

	@GetMapping("/generator-manuel")
	public String generatorManuel(Model model) {
		var options = List.of("plain", "illustrated");
		model.addAttribute("templates", options);
		var definitions = List.of("Netzwerk-def", "Verbund-def", "ComputerSystem-def");
		model.addAttribute("definitions", definitions);
		return "generator";
	}

	@GetMapping("/generator-auto")
	public String generatorAuto(Model model) {
		var options = List.of("plain", "illustrated");
		model.addAttribute("templates", options);
		return "generator-auto";
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

	private Map<String, String> getPlain(String definition) {
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

	private Map<String, String> getIllustrated() {
		return null;
	}

	@GetMapping("/generator/def")
	public String getDefinition(@RequestParam String template, @RequestParam String definition, Model model) {
		Map<String, String> attributes = switch (template) {
			case "plain" -> getPlain(definition);
			case "illustrated" -> getIllustrated();
			default -> null;
		};
		model.addAllAttributes(attributes);
		return template;
	}

	@PostMapping("generator-auto/complete")
	public String overviewGeneration(@RequestParam MultipartFile curriculum, @RequestParam String template, Model model) {
		List<String> objects;
		Object parsed;
		try {
			parsed = parseFromInputStream(curriculum.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//var file =storageService.loadAsResource(curriculum.getName());
		objects = (List<String>) parsed;

		var tempmodel = getKnowledge();

		for (String element : objects){
			findRelatedData(element);
		}
		storageService.deleteAll();
		return "overview-auto";
	}

	private void findRelatedData(String element) {
		knowledge.findAll(element);
	}
}
