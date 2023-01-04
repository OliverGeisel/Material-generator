package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import java.util.Collection;

public class Term extends TermElement{
	private String term;

	public Term(String content, String id, String type, Collection<String> relations) {
		super(content, id, type,relations);
	}
}
