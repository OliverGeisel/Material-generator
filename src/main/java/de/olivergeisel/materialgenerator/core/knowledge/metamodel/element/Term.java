package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Term extends KnowledgeElement{
	private String term;

	public Term(String content, String id, String type, Collection<KnowledgeElementRelation> relations) {
		super(content, id, type, relations);
	}
}
