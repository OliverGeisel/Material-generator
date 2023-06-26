package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeManagement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.finalization.FinalizationService;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import de.olivergeisel.materialgenerator.generation.generator.MappingRepository;
import de.olivergeisel.materialgenerator.generation.generator.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.generator.MaterialRepository;
import de.olivergeisel.materialgenerator.generation.generator.TranslateGenerator;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateSet;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateSetRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

import static de.olivergeisel.materialgenerator.generation.TemplateService.PLAIN;

@Service
public class GeneratorService {

	private final KnowledgeManagement knowledgeManagement;
	private final FinalizationService finalizationService;
	private final TemplateSetRepository templateSetRepository;
	private final MaterialRepository materialRepository;
	private final MappingRepository mappingRepository;

	public GeneratorService(KnowledgeManagement knowledgeManagement, FinalizationService finalizationService,
							TemplateSetRepository templateSetRepository, MaterialRepository materialRepository,
							MappingRepository mappingRepository) {
		this.knowledgeManagement = knowledgeManagement;
		this.finalizationService = finalizationService;
		this.templateSetRepository = templateSetRepository;
		this.materialRepository = materialRepository;
		this.mappingRepository = mappingRepository;
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
		return finalizationService.createRawCourse(coursePlan, template);
	}

	private Set<MaterialAndMapping> createMaterials(CoursePlan coursePlan, TemplateSet templateSet) {
		TranslateGenerator generator = new TranslateGenerator();
		generator.input(templateSet, knowledgeManagement.getKnowledge(), coursePlan);
		if (generator.isReady()) {
			generator.update();
		}
		var output = generator.output();
		materialRepository.saveAll(output.getAllMaterial());
		mappingRepository.saveAll(output.getAllMappings());
		return output.getMaterialAndMapping();
	}

}
