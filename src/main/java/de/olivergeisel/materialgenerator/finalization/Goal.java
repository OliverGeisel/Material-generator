package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
public class Goal {
	private final ContentGoalExpression expression;
	private final String                masterKeyword;
	@OneToMany(mappedBy = "goalId", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Topic>           topics = new LinkedList<>();
	//private final List<String> specificWords;
	@Column(length = 2_000)
	private final String                completeSentence;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private       UUID                  id;
	private       String                name;

	public Goal() {
		this.expression = ContentGoalExpression.FIRST_LOOK;
		this.masterKeyword = "";
		this.completeSentence = "";
		this.name = "";
	}


	public Goal(ContentGoal contentGoal) {
		this.expression = contentGoal.getExpression();
		this.masterKeyword = contentGoal.getMasterKeyword();
		//this.specificWords = specificWords;
		this.completeSentence = contentGoal.getCompleteSentence();
		this.name = contentGoal.getName();
		for (var contentTarget : contentGoal.getContent()) {
			topics.add(new Topic(contentTarget, this));
		}
	}

	boolean isSame(ContentGoal contentGoal) {
		return expression == contentGoal.getExpression() && masterKeyword.equals(contentGoal.getMasterKeyword())
			   && completeSentence.equals(contentGoal.getCompleteSentence())
			   && topics.size() == contentGoal.getContent().size();
	}

	//region setter/getter
	public List<Topic> getTopics() {
		return topics;
	}

	public UUID getId() {
		return id;
	}

	public ContentGoalExpression getExpression() {
		return expression;
	}

	public String getMasterKeyword() {
		return masterKeyword;
	}

	public String getCompleteSentence() {
		return completeSentence;
	}

	public String getName() {
		return name;
	}
//endregion
}
