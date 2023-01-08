package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

public abstract class StructureElementPart extends StructureElement {


	protected StructureElementPart(ContentTarget topic, Relevance relevance, String name) {
		super(topic, relevance, name);
	}
}
