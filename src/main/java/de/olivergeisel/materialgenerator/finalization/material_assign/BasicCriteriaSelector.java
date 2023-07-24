package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.finalization.Topic;
import de.olivergeisel.materialgenerator.generation.material.Material;

/**
 * Basic implementation of a criteria selector. It selects a material based on the alias of the material.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see CriteriaSelector
 * @see MaterialAssigner
 * @see Material
 * @see ContentGoal
 * @see de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget
 * @since 0.2.0
 */
public class BasicCriteriaSelector implements CriteriaSelector {
	/**
	 * Compares the given material with the criteria.
	 * <p>
	 * It only compares if the material alias satisfies the criteria. extra behavior can be defined in the
	 * implementation.
	 *
	 * @param material material to check
	 * @param criteria criteria to check
	 * @return {@literal true} if material satisfies the criteria, otherwise {@literal false}
	 */
	@Override
	public boolean satisfies(Material material, String criteria) {
		return material.getStructureId().equals(criteria);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param material material to check
	 * @param goal     goal to check
	 * @return {@literal true} if material satisfies the goal, otherwise {@literal false}
	 * @throws IllegalArgumentException if goal is null
	 */
	@Override
	public boolean satisfies(Material material, Goal goal) throws IllegalArgumentException {
		for (var topic : goal.getTopics()) {
			if (satisfies(material, topic)) return true;
		}
		return false;
	}

	@Override
	public boolean satisfies(Material material, Topic target) throws IllegalArgumentException {
		if (target == null) throw new IllegalArgumentException("target must not be null");
		var topicName = target.getName();
		var structure = material.getStructureId();

		if (topicName.equals(structure)) {
			return true;
		}
		if (topicName.contains(structure)) {
			return true;
		}
		var subNames = topicName.split(" ");
		for (var subName : subNames) {
			if (subName.equals(structure)) {
				return true;
			}
		}
		return false;
	}

}
