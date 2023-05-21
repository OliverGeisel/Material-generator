package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;

import java.util.Collection;

public class Exercise extends ComplexElement{
	public Exercise(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
	}
}
