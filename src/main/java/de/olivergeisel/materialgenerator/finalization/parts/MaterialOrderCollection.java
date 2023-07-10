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

/**
 * A MaterialOrderCollection is a collection of {@link MaterialOrderPart}s. This Type as in the lowest level
 * {@link Material}s assigned to it. It can be a {@link GroupOrder}, a {@link ChapterOrder} or a {@link TaskOrder}.
 *
 * @author Oliver Geisel
 * @version 1.0
 * @see MaterialOrderPart
 * @see GroupOrder
 * @see ChapterOrder
 * @see TaskOrder
 * @since 0.2.0
 */
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

	/**
	 * Assign a single Material to this part. Can throw {@link UnsupportedOperationException} when this operation is
	 * not supported.
	 *
	 * @param material Material to assign
	 * @return true if material is assigned, false otherwise
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	public abstract boolean assign(Material material) throws UnsupportedOperationException;

	/**
	 * Assign {@link Material} in a {@link MaterialAssigner} to this part. Can throw
	 * {@link UnsupportedOperationException} when this
	 * operation is not supported.
	 *
	 * @param assigner MaterialAssigner to assign material
	 * @return true if at least one material is assigned, false otherwise
	 * @throws UnsupportedOperationException if this operation is not supported
	 */
	public abstract boolean assignMaterial(MaterialAssigner assigner);

	/**
	 * Add an alternative name for this part at the end of the list. This alternative is the least important
	 * alternative. If the alternative is already in the list, it will not be added again.
	 *
	 * @param alternative new alternative. Must not be null or empty
	 * @return true if alternative is added, false otherwise
	 */
	public boolean appendAlias(String alternative) {
		if (alternative == null || alternative.isBlank())
			return false;
		if (alias.contains(alternative))
			return false;
		return alias.add(alternative);
	}

	/**
	 * Remove an alternative name for this part. If the alternative is not in the list, nothing happens.
	 *
	 * @param alternative alternative to remove
	 * @return true if alternative is removed, false otherwise
	 */
	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}

	/**
	 * Move an alternative name up in the list. If the alternative is not in the list or already at the top, nothing
	 * happens.
	 *
	 * @param alternative alternative to move up
	 * @return true if alternative is moved, false otherwise
	 */
	public boolean moveUp(String alternative) {
		int index = alias.indexOf(alternative);
		if (index > 0) {
			alias.remove(index);
			alias.add(index - 1, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Move an alternative name down in the list. If the alternative is not in the list or already at the bottom,
	 * nothing happens.
	 *
	 * @param alternative alternative to move down
	 * @return true if alternative is moved, false otherwise
	 */
	public boolean moveDown(String alternative) {
		int index = alias.indexOf(alternative);
		if (index < alias.size() - 1) {
			alias.remove(index);
			alias.add(index + 1, alternative);
			return true;
		}
		return false;
	}

	/**
	 * Move an alternative name to a specific position in the list. If the alternative is not in the list or the index
	 * is out of bounds, nothing happens.
	 *
	 * @param alternative alternative to move
	 * @param newIndex    new index of the alternative
	 * @return true if alternative is moved, false otherwise. Also false if the index is out of bounds
	 */
	public boolean move(String alternative, int newIndex) {
		int index = alias.indexOf(alternative);
		if (index < 0 || index >= alias.size() || newIndex < 0 || newIndex >= alias.size())
			return false;
		alias.remove(index);
		alias.add(newIndex, alternative);
		return true;
	}

	/**
	 * Insert an alternative name at a specific position in the list. If the alternative is already in the list, it
	 * will not be added again.
	 *
	 * @param alternative alternative to insert
	 * @param newIndex    new index of the alternative
	 * @return true if alternative is inserted, false otherwise. Also false if the index is out of bounds or the
	 * alternative is already in the list
	 */
	public boolean insert(String alternative, int newIndex) {
		if (newIndex < 0 || newIndex >= alias.size() || alias.contains(alternative))
			return false;
		alias.add(newIndex, alternative);
		return true;
	}

	/**
	 * Finds a {@link Material} by its id. If the material is not found, null is returned.
	 *
	 * @param materialId id of the material
	 * @return material with the id or null if not found
	 */
	public abstract Material findMaterial(UUID materialId);


	/**
	 * Removes a {@link MaterialOrderPart} from this part. If the part is not found, nothing happens.
	 *
	 * @param partId id of the part to remove
	 * @return true if part is removed, false otherwise
	 */
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
