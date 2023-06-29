package de.olivergeisel.materialgenerator.finalization;


import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.generation.generator.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.DefinitionTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class FinalizationController {

	private static final String TEMPLATE_SET_FROM_TEMPLATES_FOLDER = "../templateSets/";

	private static final String PATH = "finalization/";
	private final RawCourseRepository repository;

	private final FinalizationService service;
	private final MaterialRepository materialRepository;

	public FinalizationController(RawCourseRepository repository, FinalizationService service, MaterialRepository materialRepository) {
		this.repository = repository;
		this.service = service;
		this.materialRepository = materialRepository;
	}


	@GetMapping({"edit", "edit.html", "edit/"})
	public String editOverview(Model model) {
		model.addAttribute("courses", repository.findAll());
		return PATH + "edit";
	}

	@GetMapping({"edit/{id}",})
	public String editCourse(@PathVariable UUID id, Model model) {
		return repository.findById(id).map(course -> {
			model.addAttribute("course", course);
			model.addAttribute("RELEVANCE", Arrays.stream(Relevance.values()).filter(it -> it != Relevance.TO_SET).toList());
			return PATH + "edit-course";
		}).orElse("redirect:/edit");
	}


	@PostMapping({"edit/{id}/delete",})
	public String deleteCourse(@PathVariable UUID id) {
		repository.deleteById(id);
		return "redirect:/edit";
	}

	@PostMapping({"edit/{id}/deletePart",})
	public String deleteCoursePart(@PathVariable UUID id, @RequestParam("id") UUID partId) {
		repository.findById(id).ifPresent(course -> {
			course.getMaterialOrder().remove(partId);
			repository.save(course);
		});
		return "redirect:/edit";
	}

	@PostMapping("edit/{id}/export")
	public void exportCourse(@PathVariable UUID id, HttpServletRequest request, HttpServletResponse response) {
		service.exportCourse(id, request, response);
	}

	@PostMapping({"edit/{id}",})
	public String editCourse(@PathVariable UUID id, @RequestParam(value = "chapter", required = false) UUID parentChapterId, @RequestParam(value = "group", required = false) UUID parentGroupId, @RequestParam(value = "task", required = false) UUID parentTaskId, @RequestParam(value = "up", required = false) UUID idUp, @RequestParam(value = "down", required = false) UUID idDown, Model model) {
		if (idUp != null) {
			service.moveUp(id, parentChapterId, parentGroupId, parentTaskId, idUp);
		} else if (idDown != null) {
			service.moveDown(id, parentChapterId, parentGroupId, parentTaskId, idDown);
		}
		return repository.findById(id).map(course -> {
			model.addAttribute("course", course);
			return "redirect:/edit/" + course.getId() + "#Themen";
		}).orElse("redirect:/edit");
	}


	@GetMapping("view")
	public String viewOverview(@RequestParam("materialId") UUID materialId, @RequestParam("templateSet") String templateSet, Model model) {
		AtomicReference<String> materialType = new AtomicReference<>();
		materialRepository.findById(materialId).ifPresent(material -> {
			TemplateInfo info;
			if (material.getTemplate() == null) { // todo fix templateInfo
				info = new DefinitionTemplate();
			} else {
				info = material.getTemplate();
			}
			materialType.set(info.getTemplateType().getType());
			model.addAttribute("material", material);
		});
		return TEMPLATE_SET_FROM_TEMPLATES_FOLDER + templateSet + "/" + materialType;
	}
}
