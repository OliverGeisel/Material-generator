package de.olivergeisel.materialgenerator.core.courseplan.content;

import java.util.Objects;

/**
 * A Part of a ContentGoal. Target are small peaces, which fill a small part the {@link ContentGoal}.
 * <p>
 * Example: <br>Goal: "Learn the basics of Java"
 * <br>Targets: "if-Statement", "for-Loop", "while-Loop", "switch-Statement"
 * <p>
 * The targets are the small peaces, which are needed to reach the goal.
 * The goal is the big picture, the targets are the small peaces, which are needed to reach the goal.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see ContentGoal
 * @see ContentGoalExpression
 * @since 0.2.0
 */
public class ContentTarget {

	/**
	 * An empty ContentTarget.
	 */
	public static final ContentTarget EMPTY = new ContentTarget("NO_TARGET");

	private String      value;
	private ContentGoal relatedGoal;

	protected ContentTarget() {

	}

	/**
	 * Create a new ContentTarget.
	 *
	 * @param value the value of the target.
	 */
	public ContentTarget(String value) {
		this.value = value;
	}

	//region setter/getter
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ContentGoal getRelatedGoal() {
		return relatedGoal;
	}

	public void setRelatedGoal(ContentGoal relatedGoal) {
		this.relatedGoal = relatedGoal;
	}

	public String getTopic() {
		return value;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContentTarget that)) return false;

		if (!Objects.equals(value, that.value)) return false;
		return Objects.equals(relatedGoal, that.relatedGoal);
	}

	@Override
	public int hashCode() {
		int result = value != null ? value.hashCode() : 0;
		result = 31 * result + (relatedGoal != null ? relatedGoal.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ContentTarget{" +
			   "value='" + value + '\'' +
			   ", relatedGoal=" + relatedGoal +
			   '}';
	}
}
