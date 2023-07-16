package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.course.Meta;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A RawCourse is a {@link Course} that is not yet finalized.
 * <p>
 * A RawCourse contains a {@link CourseOrder} and a {@link CourseMetadataFinalization}.
 * It can be edited.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see Course
 * @see CourseOrder
 * @see CourseMetadataFinalization
 * @since 0.2.0
 */
@Entity
public class RawCourse extends Course {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;

	private UUID                       planId;
	private String                     templateName;
	@OneToOne(cascade = CascadeType.ALL)
	private CourseMetadataFinalization metadata;
	@OneToOne(cascade = CascadeType.ALL)
	private CourseOrder                courseOrder;

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Goal> goals;

	protected RawCourse() {
	}

	public RawCourse(CoursePlan plan, String templateName, Set<Goal> goals) {
		this.planId = plan.getId();
		this.templateName = templateName;
		this.goals = goals;
		metadata = new CourseMetadataFinalization(plan);
		courseOrder = new CourseOrder(plan, goals);
	}

	/**
	 * @return
	 */
	public int materialCount() {
		return courseOrder.materialCount();
	}

	public void changePlan(CoursePlan plan) {
		setPlanId(plan.getId());
	}

	public boolean assignMaterial(Set<MaterialAndMapping> materials) {
		return courseOrder.assignMaterial(materials.stream().map(MaterialAndMapping::material).collect(
				Collectors.toSet()));
	}

	//region setter/getter

	/**
	 * Say if a course has enough materials and fulfill all requirements to use.
	 *
	 * @return True if all requirements are fulfilled. False otherwise
	 */
	public boolean isValid() {
		return courseOrder.isValid();
	}

	public UUID getId() {
		return id;
	}

	public CourseOrder getCourseOrder() {
		return courseOrder;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Set<Goal> getGoals() {
		return goals;
	}

	public UUID getPlanId() {
		return planId;
	}

	private void setPlanId(UUID planId) {
		this.planId = planId;
	}

	public CourseMetadataFinalization getMetadata() {
		return metadata;
	}

	public void setMetadata(CourseMetadataFinalization metadata) {
		this.metadata = metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Meta getMeta() {
		return metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CourseOrder getOrder() {
		return courseOrder;
	}
//endregion
}
