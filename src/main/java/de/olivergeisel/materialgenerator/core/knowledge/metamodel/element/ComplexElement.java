package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public abstract class ComplexElement extends KnowledgeElement{

	protected ComplexElement(String content, String id, String type, Collection<String> relations) {
		super(content, id, type, relations);
	}
}
