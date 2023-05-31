package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.generation.template.GeneratorService;
import de.olivergeisel.materialgenerator.generation.template.StorageService;
import de.olivergeisel.materialgenerator.generation.template.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/generator")
public class GeneratorController {

	public static final String PLAIN = "plain";
	public static final String ILLUSTRATED = "illustrated";
	public static final List<String> OPTIONS = List.of(PLAIN, ILLUSTRATED);
	private final GeneratorService service;
	private final StorageService storageService;
	private final DowloadManager dowloadManager;

	public GeneratorController(GeneratorService service, StorageService storageService, DowloadManager dowloadManager) {
		this.service = service;
		this.storageService = storageService;
		this.dowloadManager = dowloadManager;
	}

	@GetMapping("download")
	public void generateAndDownloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		dowloadManager.createSingle("test", request, response);
	}

	@GetMapping("/download-all")
	public void generateAndDownloadTemplates(@RequestParam("name") String name, HttpServletRequest request, HttpServletResponse response) {
		dowloadManager.createZip(name, request, response);
	}

	@GetMapping("/generator-auto")
	public String generatorAuto(Model model) {
		var curriculums = storageService.loadAll().map(path -> path.getFileName().toString()).toList();
		model.addAttribute("templates", OPTIONS);
		model.addAttribute("curriculums", curriculums);
		return "generator-auto";
	}

	@GetMapping("/generator-manuel")
	public String generatorManuel(Model model) {
		model.addAttribute("templates", OPTIONS);
		var definitions = List.of("Netzwerk-def", "Verbund-def", "ComputerSystem-def");
		model.addAttribute("definitions", definitions);
		return "generator";
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

		//storageService.deleteAll();
		for (ContentTarget target : coursePlan.getTargets()) {
			// todo
			//findRelatedData(target.());
		}
		storageService.store(plan);
		return "overview-auto";
	}

	@GetMapping("def")
	public String getDefinition(@RequestParam String template, @RequestParam String definition, Model model) {
		Map<String, String> attributes = switch (template) {
			case "plain" -> service.getPlain(definition);
			case "illustrated" -> service.getIllustrated();
			default -> Map.of();
		};
		model.addAllAttributes(attributes);
		return template;
	}

	@GetMapping("def-show")
	public String showDefinition(@RequestParam String term, @RequestParam String definition, @RequestParam(required = false) String template
			, Model model) {
		model.addAttribute("term", term);
		model.addAttribute("definition", definition);
		if (template == null || template.isBlank()) {
			template = "blank";
		}
		return TemplateService.TEMPLATE_SET_FROM_TEMPLATES_FOLDER + template + "/DEFINITION";
	}

//region getter / setter
	//
//
	@GetMapping("test")
	public String getTestTemplate() {
		return "../templateSets/test";
	}
//endregion


}
