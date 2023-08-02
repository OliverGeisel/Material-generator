package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureElementPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureGroup;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a group in the final course plan.
 * <p>
 * Is a unit in the course plan. Contains a list of {@link TaskOrder}s.
 * Can be nested in other groups or chapters.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see MaterialOrderPart
 * @see ChapterOrder
 * @since 0.2.0
 */
@Entity
public class GroupOrder extends MaterialOrderCollection {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<TaskOrder> taskOrder = new LinkedList<>();

	/**
	 * Creates a new GroupOrder from a StructureGroup
	 *
	 * @param part the group to be ordered
	 * @throws IllegalArgumentException if part is null
	 */
	public GroupOrder (StructureElementPart part, Set<Goal> goals) throws IllegalArgumentException {
		if (part == null) throw new IllegalArgumentException("group must not be null");
		if (!(part instanceof StructureGroup group)) {
			throw new IllegalArgumentException("part must be a StructureGroup");
		}
		for (var task : group.getParts()) {
			if (task instanceof StructureTask sTask) {
				taskOrder.add(new TaskOrder(sTask, goals));
			} else if (task instanceof StructureGroup) {
				throw new IllegalArgumentException("nested groups are not supported");
			}
		}
		setName(group.getName());
		var groupTopic = group.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(groupTopic)))
						 .findFirst().orElse(null);
		setTopic(topic);
		part.getAlternatives().forEach(this::appendAlias);
	}

	protected GroupOrder () {

	}

	public void moveUp (TaskOrder task) {
		int index = taskOrder.indexOf(task);
		if (index > 0) {
			taskOrder.remove(index);
			taskOrder.add(index - 1, task);
		}
	}

	public void moveDown (TaskOrder task) {
		int index = taskOrder.indexOf(task);
		if (index < taskOrder.size() - 1) {
			taskOrder.remove(index);
			taskOrder.add(index + 1, task);
		}
	}

	public boolean remove (TaskOrder task) {
		return taskOrder.remove(task);
	}

	public void append (TaskOrder task) {
		taskOrder.add(task);
	}

	public MaterialOrderPart find (UUID id) {
		if (this.getId().equals(id)) return this;
		return taskOrder.stream().map(t -> t.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	@Override
	public Relevance updateRelevance () {
		return getRelevance();
	}

	public TaskOrder findTask (UUID taskId) {
		return taskOrder.stream().filter(t -> t.getId().equals(taskId)).findFirst().orElse(null);
	}

	@Override
	public Material findMaterial (UUID materialId) {
		return taskOrder.stream().map(t -> t.findMaterial(materialId)).filter(Objects::nonNull).findFirst()
						.orElse(null);
	}

	/**
	 * Assigns materials to group part.
	 *
	 * @param materials the materials to assign
	 * @return the assigned materials
	 */
	@Override
	public Set<Material> assignMaterial (Set<Material> materials) {
		return taskOrder.stream().map(t -> t.assignMaterial(materials)).flatMap(Collection::stream)
						.collect(Collectors.toSet());
	}

	/**
	 * @param material
	 * @return
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean assign (Material material) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supportet yet");
	}

	/**
	 * Assigns materials to this part.
	 *
	 * @param assigner the assigner to use
	 * @return true if materials were assigned
	 */
	@Override
	public boolean assignMaterial (MaterialAssigner assigner) {
		taskOrder.forEach(t -> t.assignMaterial(assigner));
		return true;
	}

	@Override
	public int materialCount () {
		return taskOrder.stream().mapToInt(TaskOrder::materialCount).sum();
	}

	@Override
	public boolean remove (UUID partId) {
		return taskOrder.stream().anyMatch(t -> t.remove(partId));
	}

	//region setter/getter

	/**
	 * Get the relevance of this part.
	 *
	 * @return the relevance of this part
	 */
	@Override
	public Relevance getRelevance () {
		return taskOrder.stream().map(TaskOrder::getRelevance).max(Comparator.naturalOrder()).orElse(Relevance.TO_SET);
	}

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid () {
		return taskOrder.stream().allMatch(MaterialOrderPart::isValid)
			   && taskOrder.stream().allMatch(t -> t.getRelevance().ordinal() <= getRelevance().ordinal());
	}

	public List<TaskOrder> getTaskOrder () {
		return Collections.unmodifiableList(taskOrder);
	}
//endregion

	@Override
	public String toString() {
		return "GroupOrder{" +
			   "name='" + getName() + '\'' +
			   ", id=" + getId() +
			   ", topic=" + getTopic() +
			   '}';
	}
}
