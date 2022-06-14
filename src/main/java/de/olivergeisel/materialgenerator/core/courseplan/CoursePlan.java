package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.CourseStructure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CoursePlan {
	//-----------METATDATA---------------------
	private final CourseMetadata metadata;

	//-----------CONTENT-----------------------
	private final Set<CurriculumGoal> goals;
	private final Curriculum curriculum;

	//-----------STRUCTURE---------------------
	private final CourseStructure structure;

	public CoursePlan(CourseMetadata metadata, Collection<CurriculumGoal> goals, CourseStructure structure, Curriculum curriculum) {
		this.metadata = metadata;
		this.goals = new HashSet<>();
		this.goals.addAll(goals);
		this.structure = structure;
		this.curriculum = curriculum;
	}









	public CourseMetadata getMetadata() {
		return metadata;
	}

	public Set<CurriculumGoal> getGoal() {
		return Collections.unmodifiableSet(goals);
	}

	public CourseStructure getStructure() {
		return structure;
	}

	public Curriculum getCurriculum() {
		return curriculum;
	}




}
