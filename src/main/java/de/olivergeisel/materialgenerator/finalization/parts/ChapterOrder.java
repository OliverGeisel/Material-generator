package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.finalization.material_assign.MaterialAssigner;
import de.olivergeisel.materialgenerator.generation.material.Material;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
public class ChapterOrder extends MaterialOrderCollection {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<GroupOrder> groupOrder;

	/**
	 * Creates a new ChapterOrder from a StructureChapter
	 *
	 * @param stChapter the chapter to be ordered
	 * @param goals     the goals of the course
	 * @throws IllegalArgumentException if chapter is null
	 */
	public ChapterOrder(StructureChapter stChapter, Set<Goal> goals) throws IllegalArgumentException {
		groupOrder = new LinkedList<>();
		if (stChapter == null) throw new IllegalArgumentException("chapter must not be null");
		for (var group : stChapter.getParts()) {
			groupOrder.add(new GroupOrder(group, goals));
		}
		setName(stChapter.getName());
		var chapterTopic = stChapter.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(chapterTopic)))
						 .findFirst().orElse(null);
		setTopic(topic);
		stChapter.getAlternatives().forEach(this::addAlias);
	}


	protected ChapterOrder() {
		groupOrder = new LinkedList<>();
	}

	public void moveUp(GroupOrder group) {
		int index = groupOrder.indexOf(group);
		if (index > 0) {
			groupOrder.remove(index);
			groupOrder.add(index - 1, group);
		}
	}

	public void moveDown(GroupOrder group) {
		int index = groupOrder.indexOf(group);
		if (index < groupOrder.size() - 1) {
			groupOrder.remove(index);
			groupOrder.add(index + 1, group);
		}
	}

	public boolean append(GroupOrder group) {
		return groupOrder.add(group);
	}

	public boolean remove(GroupOrder group) {
		return groupOrder.remove(group);
	}

	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return groupOrder.stream().map(g -> g.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public GroupOrder findGroup(UUID groupID) {
		return groupOrder.stream().filter(g -> g.getId().equals(groupID)).findFirst().orElse(null);
	}

	public TaskOrder findTask(UUID taskId) {
		return groupOrder.stream().map(g -> g.findTask(taskId)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public Material findMaterial(UUID materialId) {
		return groupOrder.stream().map(g -> g.findMaterial(materialId)).filter(Objects::nonNull).findFirst()
						 .orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		return getRelevance();
	}

	@Override
	public int materialCount() {
		return groupOrder.stream().mapToInt(GroupOrder::materialCount).sum();
	}

	@Override
	public boolean assignMaterial(Set<Material> materials) {
		groupOrder.forEach(g -> g.assignMaterial(materials));
		return true;
	}

	/**
	 * Assigns a materials to a part.
	 *
	 * @param assigner A MaterialAssigner that provides the materials
	 * @return true if the assignment was successful
	 */
	@Override
	public boolean assignMaterial(MaterialAssigner assigner) {
		groupOrder.forEach(g -> g.assignMaterial(assigner));
		return true;
	}

	@Override
	public boolean remove(UUID partId) {
		return groupOrder.stream().anyMatch(g -> g.remove(partId));
	}

	//region setter/getter

	/**
	 * Get the relevance of the chapter.
	 *
	 * @return the relevance of the chapter. If no relevance is set, TO_SET is returned.
	 */
	@Override
	public Relevance getRelevance() {
		return groupOrder.stream().map(GroupOrder::getRelevance).max(Comparator.comparingInt(Enum::ordinal))
						 .orElse(Relevance.TO_SET);
	}

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return groupOrder.stream().allMatch(GroupOrder::isValid)
			   && groupOrder.stream().allMatch(group -> group.getRelevance().compareTo(getRelevance()) < 1);
	}

	public List<GroupOrder> getGroupOrder() {
		return Collections.unmodifiableList(groupOrder);
	}
//endregion

}
