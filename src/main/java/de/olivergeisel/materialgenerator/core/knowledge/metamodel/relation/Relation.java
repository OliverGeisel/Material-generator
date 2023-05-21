package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

public abstract class Relation {

	private final String name;
	private String fromId;
	private String toId;
	private KnowledgeElement from;
	private KnowledgeElement to;

	protected Relation(String name, String from, String to) {
		fromId = from;
		toId = to;
		this.name = name;
	}

	//
	public KnowledgeElement getElement() {
		return from;
	}

	public KnowledgeElement getFrom() {
		return from;
	}

	public void setFrom(KnowledgeElement from) {
		this.from = from;
		fromId = from.getId();
	}

	public String getName() {
		return name;
	}

	public KnowledgeElement getTo() {
		return to;
	}

	public void setTo(KnowledgeElement to) {
		this.to = to;
		toId = to.getId();
	}

}
