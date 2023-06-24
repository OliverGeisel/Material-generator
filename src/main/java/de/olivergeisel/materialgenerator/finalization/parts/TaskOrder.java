package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.generator.Material;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class TaskOrder extends MaterialOrderCollection {


	@OneToMany
	private final List<Material> materialOrder = new LinkedList<>();

	protected TaskOrder() {
	}

	public TaskOrder(StructureTask relatedTask, Set<Goal> goals) throws IllegalArgumentException {
		if (relatedTask == null) throw new IllegalArgumentException("relatedTask must not be null");
		setName(relatedTask.getName());
		var taskTopic = relatedTask.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(taskTopic))).findFirst().orElse(null);
		setTopic(topic);
		setRelevance(relatedTask.getRelevance());
	}

	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return materialOrder.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
	}

	public void moveUp(Material material) {
		int index = materialOrder.indexOf(material);
		if (index > 0) {
			materialOrder.remove(index);
			materialOrder.add(index - 1, material);
		}
	}

	public void moveDown(Material material) {
		int index = materialOrder.indexOf(material);
		if (index < materialOrder.size() - 1) {
			materialOrder.remove(index);
			materialOrder.add(index + 1, material);
		}
	}

	public Material findMaterial(UUID materialId) {
		return materialOrder.stream().filter(m -> m.getId().equals(materialId)).findFirst().orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		return relevance;
	}

	@Override
	public int materialCount() {
		return materialOrder.size();
	}

	//region getter / setter

	public List<Material> getMaterialOrder() {
		return materialOrder;
	}

//endregion
}
