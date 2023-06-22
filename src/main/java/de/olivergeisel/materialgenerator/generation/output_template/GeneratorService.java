package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeManagement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GeneratorService {

	private final KnowledgeManagement knowledgeManagement;

	public GeneratorService(KnowledgeManagement knowledgeManagement) {
		this.knowledgeManagement = knowledgeManagement;
	}


	public Set<KnowledgeElement> getMaterials(String term) {
		return knowledgeManagement.findRelatedData(term);
	}
}
