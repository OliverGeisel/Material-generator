package de.olivergeisel.materialgenerator.core.courseplan;

public class MainFocus {

	private Comment comment;
	private CurriculumGoal goal;
	private FocusType type;

	public MainFocus(Comment comment, CurriculumGoal goals, FocusType type) {
		this.comment = comment;
		this.goal = goal;
		this.type = type;
	}

	public CurriculumGoal getGoal() {
		return goal;
	}

	private enum FocusType{

		DEFINITION,
		THEOREM,
		EXAMPLE,
		EXERCISE,
		RESSOURCE

	}
}


