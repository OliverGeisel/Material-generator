package de.olivergeisel.materialgenerator.core.courseplan.content;

import java.util.ArrayList;
import java.util.List;

/**
 * A CurriculumGoal represents the educational goals of the Courseplan in MDTea.
 * <p>
 * The Goal has an expression that is a level of complexity based on Bloom-Taxonomie. </br>
 * Every Goal has a Master-Keyword which link the goal with the structure from the knowledgemodel.
 * Normally you define an educational goal as complete sentence.
 * In the complete Sentence are more than one keyword from the knowledgemodel. So the relevant keywords are in the specific Words
 * Example:
 */
public class ContentGoal {
	private final ContentGoalExpression expression;
	private final String masterKeyword;
	private final List<ContentTarget> content;
	//private final List<String> specificWords;
	private final String completeSentence;
	private String id = "";

	public ContentGoal(ContentGoalExpression expression, String masterKeyword, List<ContentTarget> content, String completeSentence) {
		this(expression, masterKeyword, content, completeSentence, "");
	}

	public ContentGoal(ContentGoalExpression expression, String masterKeyword, List<ContentTarget> content, String completeSentence, String id) {
		this.expression = expression;
		this.masterKeyword = masterKeyword;
		//this.specificWords = specificWords;
		this.completeSentence = completeSentence;
		this.id = id;
		this.content = new ArrayList<>();
		this.content.addAll(content);
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

	//
	public String getCompleteSentence() {
		return completeSentence;
	}
//

	public String getId() {
		return id;
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
}
