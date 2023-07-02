package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class TaskOrder extends MaterialOrderCollection {


	@OneToMany(cascade = CascadeType.ALL)
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
		relatedTask.getAlternatives().forEach(this::addAlias);
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

	@Override
	public boolean assignMaterial(Set<MaterialAndMapping> materials) {
		boolean back = false;
		for (var material : materials) {
			if (getAlias().stream().anyMatch(alias -> alias.contains(material.material().getStructureId())) || getAlias().stream().anyMatch(alias -> alias.contains(material.material().getStructureId().split("-")[0].trim()))) {
				materialOrder.add(material.material());
				back = true;
			}
		}
		return back;
	}

	@Override
	public boolean remove(UUID partId) {
		return materialOrder.removeIf(m -> m.getId().equals(partId));
	}


	//region setter/getter
	public List<Material> getMaterialOrder() {
		return materialOrder;
	}
//endregion

}
