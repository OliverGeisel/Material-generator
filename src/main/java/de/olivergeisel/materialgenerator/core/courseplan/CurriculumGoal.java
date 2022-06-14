package de.olivergeisel.materialgenerator.core.courseplan;

public class CurriculumGoal {
	private CurriculumGoalExpression expression;
	private String target;
	private String specificWords;
	private String completeSentence;

	public CurriculumGoal(CurriculumGoalExpression expression, String target, String specificWords, String completeSentence) {
		this.expression = expression;
		this.target = target;
		this.specificWords = specificWords;
		this.completeSentence = completeSentence;
	}
}
