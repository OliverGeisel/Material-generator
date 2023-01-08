package de.olivergeisel.materialgenerator.generation.template;


import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class GeneratorController {

	private final GeneratorService service;
	private final StorageService storageService;


	public GeneratorController(GeneratorService service, StorageService storageService) {
		this.service = service;
		this.storageService = storageService;
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
		model.addAttribute("templates", options);
		return "generator-auto";
	}


	private Map<String, String> getIllustrated() {
		return null;
	}

	@GetMapping("/generator/def")
	public String getDefinition(@RequestParam String template, @RequestParam String definition, Model model) {
		Map<String, String> attributes = switch (template) {
			case "plain" -> service.getPlain(definition);
			case "illustrated" -> getIllustrated();
			default -> null;
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
		return "overview-auto";
	}

}
