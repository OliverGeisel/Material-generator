package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlanParser;
import de.olivergeisel.materialgenerator.finalization.parts.DownloadManager;
import de.olivergeisel.materialgenerator.generation.output_template.GeneratorService;
import de.olivergeisel.materialgenerator.generation.output_template.StorageService;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateService;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateSetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/generator")
public class GeneratorController {

	public static final String PLAIN = "blank";
	public static final String ILLUSTRATED = "color";
	public static final List<String> OPTIONS = List.of(PLAIN, ILLUSTRATED);
	public static final String UPLOAD = "UPLOAD";
	private static final String PATH = "generation/";
	private final GeneratorService service;
	private final StorageService storageService;
	private final DownloadManager downloadManager;
	private final TemplateSetRepository templateSetRepository;

	public GeneratorController(GeneratorService service, StorageService storageService, DownloadManager downloadManager,
							   TemplateSetRepository templateSetRepository) {
		this.service = service;
		this.storageService = storageService;
		this.downloadManager = downloadManager;
		this.templateSetRepository = templateSetRepository;
	}

	@GetMapping({"", "/", ".html"})
	public String generator(Model model) {
		model.addAttribute("blank", PLAIN);
		model.addAttribute("color", ILLUSTRATED);
		var templates = templateSetRepository.findAll();
		var restTemplates = templates.filter(template -> !OPTIONS.contains(template.getName())).toList();
		model.addAttribute("templates", restTemplates);
		return PATH + "template-selection";
	}

	@GetMapping("/plan-selection")
	public String generatorAuto(@RequestParam("template") String templateSetName, Model model) {
		var curriculums = new LinkedList<String>();
		curriculums.add("UPLOAD");
		curriculums.addAll(storageService.loadAll().map(path -> path.getFileName().toString()).toList());
		model.addAttribute("templateSetName", templateSetName);
		model.addAttribute("curriculums", curriculums);
		return PATH + "plan-selection";
	}

	@GetMapping("/generator-manuel")
	public String generatorManuel(Model model) {
		model.addAttribute("templates", OPTIONS);
		var definitions = List.of("Netzwerk-def", "Verbund-def", "ComputerSystem-def");
		model.addAttribute("definitions", definitions);
		return "generator";
	}

	@GetMapping("overview/{plan}")
	public String overviewGenerationId(@PathVariable String plan, @RequestParam String template, Model model) {
		return overviewGeneration(plan, template, model);
	}

	@GetMapping("overview")
	public String overviewGeneration(@RequestParam String plan, @RequestParam String template, Model model) {
		String planName;
		CoursePlanParser parser = new CoursePlanParser();
		CoursePlan coursePlan;
		var file = storageService.load(plan);
		try {
			coursePlan = parser.parseFromFile(file.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		planName = file.getFileName().toString();

		model.addAttribute("plan", planName);
		model.addAttribute("template", template);
		model.addAttribute("structure", coursePlan.getStructure());
		model.addAttribute("meta", coursePlan.getMetadata());
		return PATH + "overview-all";
	}

	@PostMapping("overview")
	public String overviewGeneration(@RequestParam MultipartFile plan, @RequestParam String curriculum, @RequestParam String template, Model model) throws IOException {
		String planName;
		if (curriculum.isBlank() || curriculum.equals(UPLOAD)) {
			CoursePlanParser parser = new CoursePlanParser();
			CoursePlan coursePlan;
			if (plan == null || plan.isEmpty()) {
				throw new IllegalArgumentException("No plan uploaded");
			}
			if (!plan.getContentType().equals("application/json")) {
				throw new RuntimeException("Wrong file type");
			}
			try {
				coursePlan = parser.parseFromFile(plan.getInputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			storageService.store(plan);
			planName = plan.getOriginalFilename();
			storageService.store(plan);
		} else {
			planName = storageService.load(curriculum).getFileName().toString();
		}
		model.addAttribute("plan", planName);
		model.addAttribute("template", template);
		CoursePlanParser parser = new CoursePlanParser();
		var completePlan = parser.parseFromFile(storageService.load(planName).toFile());
		model.addAttribute("structure", completePlan.getStructure());
		model.addAttribute("meta", completePlan.getMetadata());
		return PATH + "overview-all";
	}

	@GetMapping("generate")
	public String generate(@RequestParam String plan, @RequestParam String template) throws FileNotFoundException {
		var coursePlanFile = storageService.load(plan);
		var parser = new CoursePlanParser();
		var coursePlan = parser.parseFromFile(coursePlanFile.toFile());
		var rawcourse = service.generateRawCourse(coursePlan, template);
		return "redirect:/edit/" + rawcourse.getId();
	}

	@GetMapping("def-show")
	public String showDefinition(@RequestParam String term, @RequestParam String
			definition, @RequestParam(required = false) String template
			, Model model) {
		model.addAttribute("term", term);
		model.addAttribute("definition", definition);
		if (template == null || template.isBlank()) {
			template = PLAIN;
		}
		return TemplateService.TEMPLATE_SET_FROM_TEMPLATES_FOLDER + template + "/DEFINITION";
	}

}
