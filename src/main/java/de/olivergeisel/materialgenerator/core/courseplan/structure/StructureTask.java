package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

import java.util.Set;

public class StructureTask extends StructureElementPart {

	public StructureTask(ContentTarget topic, Relevance relevance, String name, Set<KnowledgeObject> alternatives) {
		super(topic, relevance, name, alternatives);

	}

	@Override
	public String toString() {
		return "StructureTask{" +
				"name=" + getName() +
				", topic=" + getTopic() +
				", relevance=" + relevance +
				'}';
	}

	@Override
	public void updateRelevance() {
		// Nothing to do!
	}


}
