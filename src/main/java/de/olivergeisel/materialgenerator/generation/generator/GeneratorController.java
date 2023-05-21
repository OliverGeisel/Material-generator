package de.olivergeisel.materialgenerator.generation.generator;


import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.*;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.BasicRelation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.template.GeneratorService;
import de.olivergeisel.materialgenerator.generation.template.StorageService;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/generator")
public class GeneratorController {
	private static final String KNOWLEDGE_PATH = "src/main/resources/data/knowledge/knowledgedata.json";

	private static KnowledgeModel knowledge;

	private final GeneratorService service;
	private final StorageService storageService;

	public GeneratorController(GeneratorService service, StorageService storageService) {
		this.service = service;
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
		var typeString = (String) jsonElement.get("typ");
		var type = KnowledgeType.valueOf(typeString.toUpperCase());
		var content = (String) jsonElement.get("content");
		var id = (String) jsonElement.get("id");
		Collection<Relation> relations;
		if (jsonElement.containsKey("relations")) {
			relations = new LinkedList<>();
			for (var relation : (Collection<Map<String, ?>>) jsonElement.get("relations")) {
				var relationType = (RelationType) relation.get("rel-type");
				var other = (String) relation.get("other");
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
		var curriculums = storageService.loadAll().map(path -> path.getFileName().toString()).toList();
		model.addAttribute("templates", options);
		model.addAttribute("curriculums", curriculums);
		return "generator-auto";
	}

	private Map<String, String> getPlain(String definition) {
		Map<String, String> back = new HashMap<>();
		var knowledgeModel = getKnowledge();
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

	@GetMapping("def")
	public String getDefinition(@RequestParam String template, @RequestParam String definition, Model model) {
		Map<String, String> attributes = switch (template) {
			case "plain" -> service.getPlain(definition);
			case "illustrated" -> getIllustrated();
			default -> Map.of();
		};
		model.addAllAttributes(attributes);
		return template;
	}


	@PostMapping("generator-auto/complete")
	public String overviewGeneration(@RequestParam MultipartFile plan, @RequestParam String template, Model model) {
		CoursePlanParser parser = new CoursePlanParser();
		CoursePlan coursePlan;
		try {
			coursePlan = parser.parseFromFile(plan.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//var file =storageService.loadAsResource(plan.getName());
		storageService.store(plan);
		var knowledge = service.getKnowledge();

		//storageService.deleteAll();
		for (ContentTarget target : coursePlan.getTargets()) {
			// todo
			//findRelatedData(target.());
		}
		storageService.store(plan);
		return "overview-auto";
	}

	private Set<KnowledgeElement> findRelatedData(String element) {
		return knowledge.findAll(element);

	}

	//
//
	private Map<String, String> getIllustrated() {
		return Map.of();
	}

	private static KnowledgeModel getKnowledge() {
		if (knowledge == null) {
			var parsed = pareseFromPath(KNOWLEDGE_PATH);
			knowledge = createKnowlegeModel(parsed);
		}
		return knowledge;
	}
}
