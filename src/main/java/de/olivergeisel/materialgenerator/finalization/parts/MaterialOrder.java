package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.generator.Material;
import de.olivergeisel.materialgenerator.generation.generator.MaterialAndMapping;

import javax.persistence.*;
import java.util.*;

/**
 * Order of the all Materials in the course. Structured by chapters, groups/parts and parts.
 * This is the final order of the course. It is used to generate the final course. It's editable by the user.
 */
@Entity
public class MaterialOrder {
	@OneToMany(cascade = CascadeType.ALL)
	private final List<ChapterOrder> chapterOrder = new LinkedList<>();
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;

	public MaterialOrder(CoursePlan plan, Set<Goal> goals) {
		for (var chapter : plan.getStructure().getOrder()) {
			chapterOrder.add(new ChapterOrder(chapter, goals));
		}
	}

	protected MaterialOrder() {

	}

	public int materialCount() {
		return chapterOrder.stream().mapToInt(ChapterOrder::materialCount).sum();
	}

	public MaterialOrderPart find(UUID id) {
		return chapterOrder.stream().map(c -> c.find(id)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public ChapterOrder findChapter(UUID chapterID) {
		return chapterOrder.stream().filter(c -> c.getId().equals(chapterID)).findFirst().orElse(null);
	}

	public GroupOrder findGroup(UUID groupID) {
		return chapterOrder.stream().map(c -> c.findGroup(groupID)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public TaskOrder findTask(UUID partID) {
		return chapterOrder.stream().map(c -> c.findTask(partID)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public Material findMaterial(UUID materialId) {
		return chapterOrder.stream().map(c -> c.findMaterial(materialId)).filter(Objects::nonNull).findFirst().orElse(null);
	}

	public boolean assignMaterial(Set<MaterialAndMapping> materials) {
		return chapterOrder.stream().anyMatch(c -> c.assignMaterial(materials));
	}


	public void moveUp(ChapterOrder chapter) {
		int index = chapterOrder.indexOf(chapter);
		if (index > 0) {
			chapterOrder.remove(index);
			chapterOrder.add(index - 1, chapter);
		}
	}

	public void moveDown(ChapterOrder chapter) {
		int index = chapterOrder.indexOf(chapter);
		if (index < chapterOrder.size() - 1) {
			chapterOrder.remove(index);
			chapterOrder.add(index + 1, chapter);
		}
	}

	public boolean remove(UUID partId) {
		return chapterOrder.stream().anyMatch(c -> c.remove(partId));
	}

	//region setter/getter
	public List<ChapterOrder> getChapterOrder() {
		return Collections.unmodifiableList(chapterOrder);
	}

	public UUID getId() {
		return id;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MaterialOrder that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "MaterialOrder{" + "chapterOrder=" + chapterOrder + '}';
	}
}
