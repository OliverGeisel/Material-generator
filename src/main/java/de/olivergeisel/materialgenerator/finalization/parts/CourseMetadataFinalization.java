package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;

import javax.persistence.*;
import java.util.*;

@Entity
public class CourseMetadataFinalization {
	@ElementCollection
	@CollectionTable(name = "metadata_finalization_entity_map", joinColumns = @JoinColumn(name = "entity_id"))
	@MapKeyColumn(name = "key_column")
	@Column(name = "value_column")
	private final Map<String, String> otherInfos;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;
	private String name;
	private String year;
	private String level;
	private String type;
	private String description;
	private UUID courseId;

	public CourseMetadataFinalization(CoursePlan plan) {
		this(plan.getMetadata().getName(), plan.getMetadata().getYear(), plan.getMetadata().getLevel(), plan.getMetadata().getType(), plan.getMetadata().getDescription(), plan.getMetadata().getOtherInfos());
		courseId = plan.getId();
	}

	protected CourseMetadataFinalization() {
		otherInfos = new HashMap<>();
	}

	//region setter/getter

	public CourseMetadataFinalization(Optional<String> name, Optional<String> year, Optional<String> level, Optional<String> type, Optional<String> description, Map<String, String> rest) {
		this(name.orElse(""), year.orElse(""), level.orElse(""), type.orElse(""), description.orElse(""), rest);
		otherInfos.putAll(rest);
	}

	public CourseMetadataFinalization(String name, String year, String level, String type, String description, Map<String, String> rest) {
		this.name = name;
		this.year = year;
		this.level = level;
		this.type = type;
		this.description = description;
		otherInfos = new HashMap<>();
		otherInfos.putAll(rest);
	}

	public boolean addOtherInfo(String key, String value) {
		if (key == null || value == null) {
			return false;
		}
		return otherInfos.put(key, value) != null;
	}

	public boolean removeOtherInfo(String key) {
		if (key == null) {
			return false;
		}
		return otherInfos.remove(key) != null;
	}
	public UUID getCourseId() {
		return courseId;
	}
	public UUID getId() {
		return id;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public void setCourseId(UUID courseId) {
		this.courseId = courseId;
	}

	public void setDescription(String description) {
		if (description == null) {
			throw new IllegalArgumentException("Description must not be null");
		}
		this.description = description;
	}

	public Optional<String> getLevel() {
		return Optional.ofNullable(level);
	}

	public void setLevel(String level) {
		if (level == null) {
			throw new IllegalArgumentException("Level must not be null");
		}
		this.level = level;
	}

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null");
		}
		this.name = name;
	}

	public Map<String, String> getOtherInfos() {
		return otherInfos;
	}

	public Optional<String> getType() {
		return Optional.ofNullable(type);
	}

	public void setType(String type) {
		if (type == null) {
			throw new IllegalArgumentException("Type must not be null");
		}
		this.type = type;
	}

	public Optional<String> getYear() {
		return Optional.ofNullable(year);
	}

	public void setYear(String year) {
		if (year == null) {
			throw new IllegalArgumentException("Year must not be null");
		}
		this.year = year;
	}
//endregion
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CourseMetadataFinalization that)) return false;

		if (!otherInfos.equals(that.otherInfos)) return false;
		if (!Objects.equals(name, that.name)) return false;
		if (!Objects.equals(year, that.year)) return false;
		if (!Objects.equals(level, that.level)) return false;
		if (!Objects.equals(type, that.type)) return false;
		return Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		int result = otherInfos.hashCode();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (year != null ? year.hashCode() : 0);
		result = 31 * result + (level != null ? level.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}


}
