package de.olivergeisel.materialgenerator.core.courseplan.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CourseMetadata {
	private Optional<String> name;
	private Optional<String> year;
	private Optional<String> level;
	private Optional<String> type;
	private Optional<String> description;
	private final Map<String, String> otherInfos;

	public CourseMetadata(String name, String year, String level, String type, String description, Map<String, String> rest) {
		this.name = Optional.of(name != null ? name : "");
		this.year = Optional.of(year != null ? year : "");
		this.level = Optional.of(level != null ? level : "");
		this.type = Optional.of(type != null ? type : "");
		this.description = Optional.of(description != null ? description : "");
		otherInfos = new HashMap<>();
		otherInfos.putAll(rest);
	}
}
