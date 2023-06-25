package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeManagement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.finalization.FinalizationService;
import de.olivergeisel.materialgenerator.finalization.parts.RawCourse;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GeneratorService {

	private final KnowledgeManagement knowledgeManagement;
	private final FinalizationService finalizationService;

	public GeneratorService(KnowledgeManagement knowledgeManagement,
							FinalizationService finalizationService) {
		this.knowledgeManagement = knowledgeManagement;
		this.finalizationService = finalizationService;
	}

	public Set<KnowledgeElement> getMaterials(String term) {
		return knowledgeManagement.findRelatedData(term);
	}

	public RawCourse generateRawCourse(CoursePlan coursePlan, String template) {
		return finalizationService.createRawCourse(coursePlan, template);
	}

}
