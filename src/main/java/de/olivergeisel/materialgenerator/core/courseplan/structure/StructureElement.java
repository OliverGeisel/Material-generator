package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

import java.util.HashSet;
import java.util.Set;

public abstract class StructureElement {

	private final Set<KnowledgeObject> alias = new HashSet<>();
	protected Relevance relevance;
	private ContentTarget topic;
	private String name;

	protected StructureElement(ContentTarget topic, Relevance relevance, String name, Set<KnowledgeObject> alternatives) {
		this.relevance = relevance;
		this.name = name;
		this.topic = topic;
		this.alias.addAll(alternatives);
	}

	public boolean addAlias(KnowledgeObject alternative) {
		return alias.add(alternative);
	}

	public boolean removeAlias(KnowledgeObject alternative) {
		return alias.remove(alternative);
	}

	public abstract void updateRelevance();

//region getter / setter
	//
//
	public Set<KnowledgeObject> getAlternatives() {
		return alias;
	}

	public String getName() {
		return name;
	}

	public Relevance getRelevance() {
		return relevance;
	}

	public ContentTarget getTopic() {
		return topic;
	}

	public void setTopic(ContentTarget topic) {
		this.topic = topic;
	}

	public boolean isValid() {
		return relevance != Relevance.TO_SET;
	}
//endregion

}
