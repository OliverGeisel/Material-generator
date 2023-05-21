package de.olivergeisel.materialgenerator.core.knowledge.metamodel.source;

public abstract class KnowledgeSource {
	private String id;
	private String name;

	protected KnowledgeSource(String id, String name) {
		this.id = id;
		this.name = name;
	}
}
