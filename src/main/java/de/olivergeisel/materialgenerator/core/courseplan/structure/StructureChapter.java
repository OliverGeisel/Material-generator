package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.CurriculumGoal;
import de.olivergeisel.materialgenerator.core.courseplan.Relevance;

import java.util.ArrayList;
import java.util.List;

public class StructureChapter extends StructureElement {


	private List<StructureElement> parts;

	public StructureChapter(CurriculumGoal goal, Relevance relevance, String name) {
		super(goal, relevance, name);
		parts = new ArrayList<>();
	}

	public boolean add(StructureElement element) throws IllegalArgumentException {
		if (element instanceof StructureChapter chapter) {
			throw new IllegalArgumentException(String.format("%s can't be addet to a chapter", chapter.getName()));
		}
		if (contains(element)) {
			return false;
		}
		return parts.add(element);
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

	public boolean contains(StructureElement element) {
		for (StructureElement element1 : parts) {
			if (element1 instanceof StructureGroup group && group.contains(element))
				return true;
			else {
				if (element1.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

}
