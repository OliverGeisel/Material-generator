package de.olivergeisel.materialgenerator.core.course;

import java.util.List;

public class CourseOrder {

	private List<CourseChapter> chapters;

	public boolean add(CourseChapter chapter) {
		return chapters.add(chapter);
	}


}
