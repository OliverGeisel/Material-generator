package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Acronym extends KnowledgeElement{
	public Acronym(String content, String id, String type, Collection<KnowledgeElementRelation> relations) {
		super(content, id, type, relations);
	}
}
