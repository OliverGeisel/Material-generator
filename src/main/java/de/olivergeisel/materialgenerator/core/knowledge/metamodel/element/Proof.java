package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Proof extends SimpleElement{
	public Proof(String content, String id, String type, Collection<String> relations) {
		super(content, id, type,relations);
	}
}
