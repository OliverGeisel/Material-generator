package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "topic")
public class Topic {

	private String name;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;
	private UUID goalId;

	public Topic(ContentTarget contentTarget, Goal goal) {
		if (contentTarget == null) {
			throw new IllegalArgumentException("ContentTarget must not be null");
		}
		name = contentTarget.getTopic();
		contentTarget.getValue();
		this.goalId = goal.getId();
	}

	public Topic(ContentTarget contentTarget) {
		name = contentTarget.getTopic();
		contentTarget.getValue();
	}

	protected Topic() {

	}

	public void updateGoal(Goal goal) {
		if (goal == null) {
			throw new IllegalArgumentException("Goal must not be null");
		}
		if (goal.getId() == null) {
			throw new IllegalArgumentException("Goal must have an ID");
		}
		this.goalId = goal.getId();
	}

	public boolean isSame(ContentTarget contentTarget) {
		return name.equals(contentTarget.getTopic());
	}

	//region setter/getter
	//region getter / setter
	public String getName() {
		return name;
	}

	public UUID getId() {
		return id;
	}

	public UUID getGoalId() {
		return goalId;
	}
//endregion
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Topic topic)) return false;

		if (!id.equals(topic.id)) return false;
		return goalId.equals(topic.goalId);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + goalId.hashCode();
		return result;
	}
}