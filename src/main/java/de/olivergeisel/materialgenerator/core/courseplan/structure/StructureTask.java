package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.CurriculumGoal;
import de.olivergeisel.materialgenerator.core.courseplan.Relevance;

public class StructureTask extends StructureElementPart {

	private CurriculumGoal topic;

	public StructureTask(CurriculumGoal goal, Relevance relevance, String name) {
		super(relevance, name);
		topic = goal;
	}

	@Override
	public String toString() {
		return "StructureTask{" +
				"name=" + getName() +
				", goal=" + topic +
				", relevance=" + relevance +
				'}';
	}

	@Override
	public void updateRelevance() {
		// Nothing to do!
	}

//
	public CurriculumGoal getGoal() {
		return topic;
	}
//
}
