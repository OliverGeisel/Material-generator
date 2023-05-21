package de.olivergeisel.materialgenerator.core.courseplan.content;

/**
 * A Part of a ContentGoal. Target are small peaces, which fill a small part the goal.
 */
public class ContentTarget {

	public static final ContentTarget EMPTY = new ContentTarget("NO_TARGET");
	private final String id;
	private String value;

	public ContentTarget(String value) {
		this.value = value;
		id = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContentTarget that)) return false;

		if (!id.equals(that.id)) return false;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}

//
	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
//
}
