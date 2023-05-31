package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

import java.util.Set;

public abstract class StructureElementPart extends StructureElement {

	protected StructureElementPart(ContentTarget topic, Relevance relevance, String name, Set<KnowledgeObject> alternatives) {
		super(topic, relevance, name, alternatives);
	}
}
