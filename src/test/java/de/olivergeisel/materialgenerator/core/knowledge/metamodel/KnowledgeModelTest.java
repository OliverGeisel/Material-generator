package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.RootStructureElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class KnowledgeModelTest {

	private KnowledgeModel knowledgeModel;

	@BeforeEach
	void setUp() {
		knowledgeModel = new KnowledgeModel();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void createKnowledgeModel() {
		var knowledgeModel = new KnowledgeModel();
	}

	@Test
	void createWithParameter() {
		var root = mock(RootStructureElement.class);
		var knowledgeModel = new KnowledgeModel(root);
	}

	@Test
	void creteWithRootIsNull() {
		assertThrows(IllegalArgumentException.class, () -> new KnowledgeModel(null));
	}

}