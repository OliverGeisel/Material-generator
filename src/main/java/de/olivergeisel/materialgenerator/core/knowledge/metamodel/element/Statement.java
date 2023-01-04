package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Statement extends SimpleElement{
	public Statement(String content, String id, String type, Collection<String> relations) {
		super(content, id, type, relations);
	}
}
