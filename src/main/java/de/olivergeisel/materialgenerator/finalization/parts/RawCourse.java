package de.olivergeisel.materialgenerator.finalization.parts;


import de.olivergeisel.materialgenerator.core.course.Course;
import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.finalization.Goal;
import de.olivergeisel.materialgenerator.generation.generator.MaterialAndMapping;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class RawCourse extends Course {
	private UUID planId;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id = UUID.randomUUID();
	private String templateName;
	@OneToOne(cascade = CascadeType.ALL)
	private CourseMetadataFinalization metadata;
	@OneToOne(cascade = CascadeType.ALL)
	private MaterialOrder materialOrder;

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Goal> goals;

	protected RawCourse() {
	}

	public RawCourse(CoursePlan plan, String templateName, Set<Goal> goals) {
		this.planId = plan.getId();
		this.templateName = templateName;
		this.goals = goals;
		metadata = new CourseMetadataFinalization(plan);
		materialOrder = new MaterialOrder(plan, goals);
	}

	public int materialCount() {
		return materialOrder.materialCount();
	}

	public void changePlan(CoursePlan plan) {
		setPlanId(plan.getId());
	}

	//region getter / setter
	public UUID getId() {
		return id;
	}


	public boolean assignMaterial(Set<MaterialAndMapping> materials) {
		return materialOrder.assignMaterial(materials);
	}

	public MaterialOrder getMaterialOrder() {
		return materialOrder;
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
//endregion
}
