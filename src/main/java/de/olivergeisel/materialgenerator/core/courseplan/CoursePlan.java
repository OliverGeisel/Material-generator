package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.CourseStructure;

import java.util.*;

public class CoursePlan {
	//-----------METATDATA---------------------
	private final CourseMetadata metadata;

	//-----------CONTENT-----------------------
	private final Set<ContentGoal> goals;
	private final List<ContentTarget> targets;
	//-----------STRUCTURE---------------------
	private final CourseStructure structure;

	public CoursePlan(CourseMetadata metadata, Collection<ContentGoal> goals, CourseStructure structure, Collection<ContentTarget> targets) {
		this.metadata = metadata;
		this.goals = new HashSet<>();
		this.goals.addAll(goals);
		this.structure = structure;
		this.targets = new ArrayList<>();
		this.targets.addAll(targets);

	}

//
public Set<ContentGoal> getGoals() {
	return goals;
}

	public CourseMetadata getMetadata() {
		return metadata;
	}

	public CourseStructure getStructure() {
		return structure;
	}

	public List<ContentTarget> getTargets() {
		return targets;
	}
//


}
