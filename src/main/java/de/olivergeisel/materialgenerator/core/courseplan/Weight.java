package de.olivergeisel.materialgenerator.core.courseplan;

public enum Weight {

	/**
	 * Not part of course but extra knowledge that can help to understand the topic
	 */
	EXTRA,
	/**
	 * Part of the topic but not essential part and can be skipped by the user.
	 */
	SKIPPABLE,
	/**
	 * Part of the course but if it's missing a warning should appear.
	 */
	IMPORTANT,
	/**
	 * Without these part the course isn't complete and can't be created.
	 */
	MANDATORY,

}
