package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations.Relation;

import java.util.Set;

public class Example extends KnowledgeElement {
	public Example(String content, String id, String type, Set<Relation> relations) {
		super(content, id, type, relations);
	}
}
