package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Exercise extends KnowledgeElement{
	public Exercise(String content, String id, String type, Collection<KnowledgeElementRelation> relations) {
		super(content, id, type, relations);
	}
}
