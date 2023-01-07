package de.olivergeisel.materialgenerator.core.courseplan;

import java.util.Collections;
import java.util.List;

/**
 * A CurriculumGoal represents the educational goals of the Courseplan in MDTea.
 * <p>
 * The Goal has an expression that is a level of complexity based on Bloom-Taxonomie. </br>
 * Every Goal has a Master-Keyword (target) which link the goal with the structure from the knowledgemodel.
 * Normally you define an educational goal as complete sentence.
 * Example:
 */
public class CurriculumGoal {
	private final CurriculumGoalExpression expression;
	private final String target;
	private final List<String> specificWords;
	private final String completeSentence;
	private String id = "";

	public CurriculumGoal(CurriculumGoalExpression expression, String target, List<String> specificWords, String completeSentence) {
		this(expression, target, specificWords, completeSentence, "");
	}

	public CurriculumGoal(CurriculumGoalExpression expression, String target, List<String> specificWords, String completeSentence, String id) {
		this.expression = expression;
		this.target = target;
		this.specificWords = specificWords;
		this.completeSentence = completeSentence;
		this.id = id;
	}

//
	public String getCompleteSentence() {
		return completeSentence;
	}

	public CurriculumGoalExpression getExpression() {
		return expression;
	}

	public String getId() {
		return id;
	}

	public List<String> getSpecificWords() {
		return Collections.unmodifiableList(specificWords);
	}

	public String getTarget() {
		return target;
	}
//
}
