package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.Set;


public class StructureTask extends StructureElementPart {

	public StructureTask(ContentTarget topic, Relevance relevance, String name, Set<String> alternatives) {
		super(topic, relevance, name, alternatives);

	}

	protected StructureTask() {
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
	public Relevance updateRelevance() {
		// Nothing to do!
		return relevance;
	}


}
