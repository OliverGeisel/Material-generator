package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.Set;

public abstract class StructureElementPart extends StructureElement {

	protected StructureElementPart(ContentTarget topic, Relevance relevance, String name, Set<String> alternatives) {
		super(topic, relevance, name, alternatives);
	}

	protected StructureElementPart() {
		super();
	}
}
