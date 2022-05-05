package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Fact extends KnowledgeElement{
	public Fact(String content, String id, String type, Collection<String> relations) {
		super(content, id, type,relations);
	}
}
