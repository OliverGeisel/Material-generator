package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class CustomElement extends KnowledgeElement{
	private String name;

	protected CustomElement(String content, String id, String type, Collection<String> relations, String name) {
		super(content, id, type, relations);
		this.name = name;
	}


	public String getName() {
		return name;
	}
}
