package de.olivergeisel.materialgenerator.core.courseplan.content;

import java.util.ArrayList;
import java.util.List;

/**
 * A CurriculumGoal represents the educational goals of the Courseplan in MDTea.
 * <p>
 * The Goal has an expression that is a level of complexity based on Bloom-Taxonomie. </br>
 * Every Goal has a Master-Keyword which link the goal with the structure from the knowledgemodel.
 * Normally you define an educational goal as complete sentence.
 * In the complete Sentence are more than one keyword from the knowledgemodel. So the relevant keywords are in the
 * specific Words
 * Example:
 */
public class ContentGoal {

	private final ContentGoalExpression expression;
	private final String                masterKeyword;
	private final List<ContentTarget>   content;
	//private final List<String> specificWords;
	private final String                completeSentence;
	private       String                name;

	protected ContentGoal() {
		this.expression = ContentGoalExpression.FIRST_LOOK;
		this.masterKeyword = "";
		this.completeSentence = "";
		this.content = new ArrayList<>();
		this.name = "";
	}

	public ContentGoal(ContentGoalExpression expression, String masterKeyword, List<ContentTarget> content,
					   String completeSentence) {
		this(expression, masterKeyword, content, completeSentence, "");
	}

	public ContentGoal(ContentGoalExpression expression, String masterKeyword, List<ContentTarget> content,
					   String completeSentence, String name) {
		this.expression = expression;
		this.masterKeyword = masterKeyword;
		//this.specificWords = specificWords;
		this.completeSentence = completeSentence;
		this.content = new ArrayList<>();
		this.content.addAll(content);
		this.name = name;
	}

	public boolean add(ContentTarget target) {
		if (content.contains(target)) {
			return false;
		}
		return content.add(target);
	}

	public boolean remove(ContentTarget target) {
		return content.remove(target);
	}

	//region setter/getter
	public String getCompleteSentence() {
		return completeSentence;
	}

	public List<ContentTarget> getContent() {
		return content;
	}

	/*public List<String> getSpecificWords() {
		return Collections.unmodifiableList(specificWords);
	}*/
	public ContentGoalExpression getExpression() {
		return expression;
	}

	public String getMasterKeyword() {
		return masterKeyword;
	}

	public String getName() {
		return name;
	}
//endregion
}
