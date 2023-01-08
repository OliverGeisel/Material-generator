package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

public class StructureTask extends StructureElementPart {


	public StructureTask(ContentTarget topic, Relevance relevance, String name) {
		super(topic, relevance, name);

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
