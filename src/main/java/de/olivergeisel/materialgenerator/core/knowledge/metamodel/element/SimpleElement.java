package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public abstract class  SimpleElement extends KnowledgeElement {
	protected SimpleElement(String content, String id, String type, Collection<String> relations) {
		super(content, id, type, relations);
	}
}
