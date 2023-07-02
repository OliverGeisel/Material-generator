package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.Topic;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public abstract class MaterialOrderCollection extends MaterialOrderPart {

	@ElementCollection
	private final Set<String> alias = new HashSet<>(); // KnowledgeObject ids
	protected Relevance relevance;

	@ManyToOne
	private Topic topic;

	public abstract Relevance updateRelevance();

	public abstract int materialCount();

	public abstract boolean assignMaterial(Set<MaterialAndMapping> materials);

	public boolean addAlias(String alternative) {
		return alias.add(alternative);
	}

	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}

	public abstract boolean remove(UUID partId);


	//region setter/getter
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
