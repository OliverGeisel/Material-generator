package de.olivergeisel.materialgenerator.core.courseplan;


/**
 *
 */
public class LearningArea {
	// Deutsch Lernbereich
	private final int time;
	private final String description;
	private Curriculum curriculum;

	public LearningArea(int time, String description, Curriculum curriculum) {
		this.time = time;
		this.description = description;
		this.curriculum = curriculum;
	}




}
