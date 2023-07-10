package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.finalization.Topic;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.*;

@Entity
public abstract class MaterialOrderCollection extends MaterialOrderPart {

	@ElementCollection
	private final List<String> alias = new ArrayList<>(); // KnowledgeObject (Structure) ids

	@ManyToOne
	private Topic topic;

	protected MaterialOrderCollection() {
		super();
	}

	public abstract Relevance updateRelevance();

	public abstract int materialCount();

	/**
	 * Assign a set of Materials to this part. Can throw {@link UnsupportedOperationException} when no MAterial can
	 * assigned direct to part.
	 *
	 * @param materials Materials to assign
	 * @return materials that are assigned to the part
	 */
	public abstract Set<Material> assignMaterial(Set<Material> materials);

	public abstract boolean assign(Material materials);

	public abstract boolean assignMaterial(MaterialAssigner assigner);

	public boolean appendAlias(String alternative) {
		if (alternative == null || alternative.isBlank())
			return false;
		return alias.add(alternative);
	}

	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}

	public boolean moveUp(String alternative) {
		int index = alias.indexOf(alternative);
		if (index > 0) {
			alias.remove(index);
			alias.add(index - 1, alternative);
			return true;
		}
		return false;
	}

	public boolean moveDown(String alternative) {
		int index = alias.indexOf(alternative);
		if (index < alias.size() - 1) {
			alias.remove(index);
			alias.add(index + 1, alternative);
			return true;
		}
		return false;
	}

	public boolean move(String alternative, int newIndex) {
		int index = alias.indexOf(alternative);
		if (index < 0 || index >= alias.size() || newIndex < 0 || newIndex >= alias.size())
			return false;
		alias.remove(index);
		alias.add(newIndex, alternative);
		return true;
	}

	public boolean insert(String alternative, int newIndex) {
		if (newIndex < 0 || newIndex >= alias.size())
			return false;
		alias.add(newIndex, alternative);
		return true;
	}

	public abstract boolean remove(UUID partId);


	//region setter/getter
	public abstract Relevance getRelevance();

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	/**
	 * Returns a list of all aliases of this part. The order of the list say what is the most important alias
	 * aliases in the course
	 * The list is unmodifiable.
	 *
	 * @return set of aliases
	 */
	public Set<String> getAlias() {
		return Collections.unmodifiableSet(new LinkedHashSet<>(alias));
	}
//endregion
}
