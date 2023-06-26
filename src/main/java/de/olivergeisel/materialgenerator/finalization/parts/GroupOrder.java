package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureElementPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureGroup;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.generator.Material;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.*;

@Entity

public class GroupOrder extends MaterialOrderCollection {

	@OneToMany
	private final List<TaskOrder> taskOrder = new LinkedList<>();

	/**
	 * Creates a new GroupOrder from a StructureGroup
	 *
	 * @param part the group to be ordered
	 * @throws IllegalArgumentException if part is null
	 */
	public GroupOrder(StructureElementPart part, Set<Goal> goals) throws IllegalArgumentException {
		if (part == null) throw new IllegalArgumentException("group must not be null");
		if (!(part instanceof StructureGroup group))
			throw new IllegalArgumentException("part must be a StructureGroup");
		for (var task : group.getParts()) {
			if (task instanceof StructureTask sTask) {
				taskOrder.add(new TaskOrder(sTask, goals));
			} else if (task instanceof StructureGroup) {
				throw new IllegalArgumentException("nested groups are not supported");
			}
		}
		setRelevance(group.getRelevance());
		setName(group.getName());
		var groupTopic = group.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(groupTopic))).findFirst().orElse(null);
		setTopic(topic);
	}

	protected GroupOrder() {

	}

	public void moveUp(TaskOrder task) {
		int index = taskOrder.indexOf(task);
		if (index > 0) {
			taskOrder.remove(index);
			taskOrder.add(index - 1, task);
		}
	}

	public void moveDown(TaskOrder task) {
		int index = taskOrder.indexOf(task);
		if (index < taskOrder.size() - 1) {
			taskOrder.remove(index);
			taskOrder.add(index + 1, task);
		}
	}

	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return taskOrder.stream().map(t -> t.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public TaskOrder findTask(UUID taskId) {
		return taskOrder.stream().filter(t -> t.getId().equals(taskId)).findFirst().orElse(null);
	}

	public Material findMaterial(UUID materialId) {
		return taskOrder.stream().map(t -> t.findMaterial(materialId)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		for (var task : taskOrder) {
			Relevance taskRelevance = task.updateRelevance();
			if (taskRelevance.ordinal() > relevance.ordinal()) {
				relevance = taskRelevance;
			}
		}
		return relevance;
	}

	@Override
	public int materialCount() {
		return taskOrder.stream().mapToInt(TaskOrder::materialCount).sum();
	}

	//region getter / setter
	public List<TaskOrder> getTaskOrder() {
		return Collections.unmodifiableList(taskOrder);
	}
//endregion


}