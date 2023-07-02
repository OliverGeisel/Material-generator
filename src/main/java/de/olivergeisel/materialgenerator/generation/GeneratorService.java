package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeManagement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.finalization.FinalizationService;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.generator.TranslateGenerator;
import de.olivergeisel.materialgenerator.generation.material.MappingRepository;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSetRepository;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.BasicTemplateRepository;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static de.olivergeisel.materialgenerator.generation.TemplateService.PLAIN;

@Service
@Transactional
public class GeneratorService {

	private final KnowledgeManagement knowledgeManagement;
	private final FinalizationService finalizationService;
	private final TemplateSetRepository templateSetRepository;
	private final MaterialRepository materialRepository;
	private final MappingRepository mappingRepository;
	private final TemplateInfoRepository templateInfoRepository;
	private final BasicTemplateRepository basicTemplateRepository;

	public GeneratorService(KnowledgeManagement knowledgeManagement, FinalizationService finalizationService, TemplateSetRepository templateSetRepository, MaterialRepository materialRepository, MappingRepository mappingRepository, TemplateInfoRepository templateInfoRepository, BasicTemplateRepository basicTemplateRepository) {
		this.knowledgeManagement = knowledgeManagement;
		this.finalizationService = finalizationService;
		this.templateSetRepository = templateSetRepository;
		this.materialRepository = materialRepository;
		this.mappingRepository = mappingRepository;
		this.templateInfoRepository = templateInfoRepository;
		this.basicTemplateRepository = basicTemplateRepository;
	}

	public Set<KnowledgeElement> getMaterials(String term) {
		return knowledgeManagement.findRelatedData(term);
	}

	public RawCourse generateRawCourse(CoursePlan coursePlan, String template) {
		if (templateSetRepository.findByName(template).isEmpty()) {
			template = PLAIN;
		}
		var templateSet = templateSetRepository.findByName(template).orElseThrow();
		var materials = createMaterials(coursePlan, templateSet);
		return finalizationService.createRawCourse(coursePlan, template, materials);
	}

	private Set<MaterialAndMapping> createMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		TranslateGenerator generator = new TranslateGenerator();
		generator.setBasicTemplateInfo(basicTemplateRepository.findAll().toSet());
		generator.input(templateSet, knowledgeManagement.getKnowledge(), coursePlan);
		if (generator.isReady()) {
			generator.update();
		}
		var output = generator.output();
		templateInfoRepository.saveAll(output.getAllMaterial().stream().map(Material::getTemplateInfo).toList());
		materialRepository.saveAll(output.getAllMaterial());
		mappingRepository.saveAll(output.getAllMappings());
		return output.getMaterialAndMapping();
	}

}
