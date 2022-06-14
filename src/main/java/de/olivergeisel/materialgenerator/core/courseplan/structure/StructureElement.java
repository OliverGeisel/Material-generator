package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.CurriculumGoal;
import de.olivergeisel.materialgenerator.core.courseplan.Relevance;

public abstract class StructureElement {


	private CurriculumGoal goal;
	private final Relevance relevance;

	private String name;

	protected StructureElement(CurriculumGoal goal, Relevance relevance, String name) {
		this.goal = goal;
		this.relevance = relevance;
		this.name = name;
	}

	public CurriculumGoal getGoal() {
		return goal;
	}

	public Relevance getWeight() {
		return relevance;
	}

	public String getName() {
		return name;
	}


}
