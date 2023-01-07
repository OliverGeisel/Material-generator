package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

public abstract class Relation {

	private final String name;
	private KnowledgeElement from;
	private KnowledgeElement to;

	//
	public KnowledgeElement getFrom() {
		return from;
	}

	protected Relation(String name) {
		this.name = name;
	}

	public void setFrom(KnowledgeElement from) {
		this.from = from;
	}

	public String getName() {
		return name;
	}

	public KnowledgeElement getTo() {
		return to;
	}

	public void setTo(KnowledgeElement to) {
		this.to = to;
	}

}
