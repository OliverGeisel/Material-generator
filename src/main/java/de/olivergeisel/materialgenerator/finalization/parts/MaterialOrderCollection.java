package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.Topic;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
public abstract class MaterialOrderCollection extends MaterialOrderPart {

	@ElementCollection
	private final Set<String> alias = new HashSet<>(); // KnowledgeObject ids
	protected Relevance relevance;

	@ManyToOne
	private Topic topic;

	public abstract Relevance updateRelevance();

	public abstract int materialCount();

	public boolean addAlias(String alternative) {
		return alias.add(alternative);
	}

	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}


	//region getter / setter
	public Relevance getRelevance() {
		return relevance;
	}

	public void setRelevance(Relevance relevance) {
		this.relevance = relevance;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Set<String> getAlias() {
		return alias;
	}
//endregion
}
