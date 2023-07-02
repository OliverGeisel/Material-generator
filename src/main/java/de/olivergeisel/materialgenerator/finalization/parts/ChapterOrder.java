package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

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
	 * @param chapter the chapter to be ordered
	 * @param goals   the goals of the course
	 * @throws IllegalArgumentException if chapter is null
	 */
	public ChapterOrder(StructureChapter chapter, Set<Goal> goals) throws IllegalArgumentException {
		groupOrder = new LinkedList<>();
		if (chapter == null) throw new IllegalArgumentException("chapter must not be null");
		for (var group : chapter.getParts()) {
			groupOrder.add(new GroupOrder(group, goals));
		}
		setRelevance(chapter.getRelevance());
		setName(chapter.getName());
		var chapterTopic = chapter.getTopic();
		var topic = goals.stream().flatMap(goal -> goal.getTopics().stream().filter(t -> t.isSame(chapterTopic))).findFirst().orElse(null);
		setTopic(topic);
		chapter.getAlternatives().forEach(this::addAlias);
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

	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return groupOrder.stream().map(g -> g.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	//region setter/getter

	public GroupOrder findGroup(UUID groupID) {
		return groupOrder.stream().filter(g -> g.getId().equals(groupID)).findFirst().orElse(null);
	}

	public TaskOrder findTask(UUID groupId) {
		return groupOrder.stream().map(g -> g.findTask(groupId)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public Material findMaterial(UUID materialId) {
		return groupOrder.stream().map(g -> g.findMaterial(materialId)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	@Override
	public Relevance updateRelevance() {
		for (var group : groupOrder) {
			Relevance groupRelevance = group.updateRelevance();
			if (groupRelevance.ordinal() > relevance.ordinal()) {
				relevance = groupRelevance;
			}
		}
		return relevance;
	}

	@Override
	public int materialCount() {
		return groupOrder.stream().mapToInt(GroupOrder::materialCount).sum();
	}

	@Override
	public boolean assignMaterial(Set<MaterialAndMapping> materials) {
		return groupOrder.stream().anyMatch(g -> g.assignMaterial(materials));
	}

	@Override
	public boolean remove(UUID partId) {
		return groupOrder.stream().anyMatch(g -> g.remove(partId));
	}
	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return groupOrder.stream().allMatch(MaterialOrderPart::isValid)
				&& groupOrder.stream().allMatch(group -> group.relevance.ordinal() <= relevance.ordinal());
	}
	public List<GroupOrder> getGroupOrder() {
		return Collections.unmodifiableList(groupOrder);
	}
//endregion
//endregion
}
