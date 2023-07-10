package de.olivergeisel.materialgenerator.core.courseplan;

import org.apache.tomcat.util.json.ParseException;

public class CoursePlanParserException extends RuntimeException {
	public CoursePlanParserException(String message, ParseException e) {
		super(message, e);
	}

	public CoursePlanParserException(String message) {
		super(message);
	}

	public CoursePlanParserException(Exception e) {
		super(e);
	}

}
