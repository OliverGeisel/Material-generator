package de.olivergeisel.materialgenerator.core.course;

import java.util.List;

public class CourseChapter extends CousrseElement {

	private List<CourseChapterPart> parts;

	public boolean add(CourseChapterPart part) {
		return parts.add(part);
	}
}
