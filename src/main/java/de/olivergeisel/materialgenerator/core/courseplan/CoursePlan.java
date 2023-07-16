package de.olivergeisel.materialgenerator.core.courseplan;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.courseplan.meta.CourseMetadata;
import de.olivergeisel.materialgenerator.core.courseplan.structure.CourseStructure;

import java.util.*;

/**
 * A CoursePlan is one of the most important objects of the application. It contains all information about the course.
 * <p>
 * A CoursePlan contains a {@link CourseMetadata}, a {@link CourseStructure}, a set of {@link ContentGoal}s and a
 * list of {@link ContentTarget}s.
 * <br>
 * The {@link CourseMetadata} contains all information about the course like the name, the year, the level and so
 * on.
 * <br>
 * The {@link CourseStructure} contains the structure of the course. The structure is a tree of
 * {@link de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter}s which contains groups,
 * and they contain tasks.
 * <br>
 * The {@link ContentGoal}s are the educational goals of the course.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see CourseMetadata
 * @see CourseStructure
 * @see ContentGoal
 * @see ContentTarget
 * @since 0.2.0
 */
public class CoursePlan {

	private final UUID id = UUID.randomUUID();

	//-----------METATDATA---------------------
	private final CourseMetadata metadata;

	//-----------CONTENT-----------------------
	private final Set<ContentGoal>    goals;
	private final List<ContentTarget> targets;

	//-----------STRUCTURE---------------------
	private final CourseStructure structure;

	public CoursePlan(CourseMetadata metadata, Collection<ContentGoal> goals, CourseStructure structure,
			Collection<ContentTarget> targets) {
		this.metadata = metadata;
		this.goals = new HashSet<>();
		this.goals.addAll(goals);
		this.structure = structure;
		this.targets = new ArrayList<>();
		this.targets.addAll(targets);

	}

	//region setter/getter
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

	public UUID getId() {
		return id;
	}
//endregion
}
