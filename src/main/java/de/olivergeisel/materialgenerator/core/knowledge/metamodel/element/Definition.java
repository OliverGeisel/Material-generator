package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Definition extends Explanation{

	public Definition(String content, String id, String type, Collection<KnowledgeElementRelation> relations) {
		super(content, id, type, relations);
	}
}
