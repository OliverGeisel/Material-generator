package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.generator.Material;
import de.olivergeisel.materialgenerator.generation.generator.MaterialAndMapping;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
public class ChapterOrder extends MaterialOrderCollection {

	@OneToMany(cascade = CascadeType.ALL)
	private final List<GroupOrder> groupOrder;

	public ChapterOrder(StructureChapter chapter, Set<Goal> goals) {
		groupOrder = new LinkedList<>();
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

//region setter/getter
	//region getter / setter
	public List<GroupOrder> getGroupOrder() {
		return Collections.unmodifiableList(groupOrder);
	}
//endregion
//endregion
}
