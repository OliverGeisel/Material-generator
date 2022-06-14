package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.CurriculumGoal;
import de.olivergeisel.materialgenerator.core.courseplan.Relevance;

import java.util.ArrayList;
import java.util.List;

public class StructureGroup extends StructureElement {

	private List<StructureElement> parts;

	public StructureGroup(CurriculumGoal goal, Relevance relevance, String name) {
		super(goal, relevance, name);
		parts = new ArrayList<>();
	}

	public boolean add(StructureElement element) throws IllegalArgumentException {
		if (element instanceof StructureChapter chapter) {
			throw new IllegalArgumentException(String.format("%s can't be addet to a group", chapter.getName()));
		}
		if (element == this || contains(element)) {
			return false;
		}
		return parts.add(element);
	}

	public boolean contains(StructureElement element) {
		for (StructureElement part : parts) {
			if (part instanceof StructureGroup group && group.contains(element))
				return true;
			else {
				if (part.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

	public int size() {
		int back = 0;
		for (StructureElement element : parts) {
			if (element instanceof StructureGroup group) {
				back += group.size();
			} else {
				back++;
			}
		}
		return back;
	}
}
