package de.olivergeisel.materialgenerator.core.courseplan.structure;


import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StructureElement {

	private ContentTarget topic;

	protected StructureElement(ContentTarget topic, Relevance relevance, String name) {
		this.relevance = relevance;
		this.name = name;
		this.topic = topic;
	}

//
	private final List<KnowledgeStructure> areas = new ArrayList<>();
	protected Relevance relevance;
//

	public boolean addAlias(KnowledgeStructure alias) {
		return areas.add(alias);
	}

	public boolean removeAlias(KnowledgeStructure alias) {
		return areas.remove(alias);
	}

	public abstract void updateRelevance();

	private String name;
	public ContentTarget getTopic() {
		return topic;
	}
	public List<KnowledgeStructure> getAreas() {
		return Collections.unmodifiableList(areas);
	}

	public Relevance getRelevance() {
		return relevance;
	}

	public boolean isValid() {
		return relevance != Relevance.TO_SET;
	}

	public void setTopic(ContentTarget topic) {
		this.topic = topic;
	}

	public String getName() {
		return name;
	}


}
