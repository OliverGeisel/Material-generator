package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Synonym extends KnowledgeElement{

	public Synonym(String content, String id, String type, Collection<String> relations) {
		super(content, id, type,relations);
	}
}
