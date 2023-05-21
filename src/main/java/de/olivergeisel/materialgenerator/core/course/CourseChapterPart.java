package de.olivergeisel.materialgenerator.core.course;

import java.util.List;

public abstract class CourseChapterPart extends CousrseElement {


}

abstract class CourseChapterCollectionPart extends CourseChapterPart {
	protected List<CourseChapterPart> parts;

	public abstract boolean contains(CourseChapterPart part);
}

abstract class CourseChapterLeafPart extends CourseChapterPart {

}