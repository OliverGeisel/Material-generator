package de.olivergeisel.materialgenerator.core.courseplan;

public class MainFocus {
	// Deutsch Schwerpunkt

	private Comment comment;
	private CurriculumGoal goals;
	private FocusType type;

	public MainFocus(Comment comment, CurriculumGoal goals, FocusType type) {
		this.comment = comment;
		this.goals = goals;
		this.type = type;
	}

//
	public CurriculumGoal getGoals() {
		return goals;
	}
//

	private enum FocusType {

		DEFINITION,
		THEOREM,
		EXAMPLE,
		EXERCISE,
		RESSOURCE

	}
}


