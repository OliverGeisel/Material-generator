package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains all Knowledge from a KnowledgeModel, that is related to a specific topic in the structure.
 * Each Node has a main KnowledgeElement. It should be a Term
 */
public class KnowledgeNode {

	private KnowledgeObject structurePoint;
	private KnowledgeElement mainElement;
	private KnowledgeElement[] relatedElements;
	private Relation[] relations;
	private Optional<ContentGoal> goal = Optional.empty();

	public KnowledgeNode(KnowledgeObject structurePoint, KnowledgeElement mainElement, KnowledgeElement[] relatedElements, Relation[] relations) {
		this.structurePoint = structurePoint;
		this.mainElement = mainElement;
		this.relatedElements = relatedElements;
		this.relations = relations;
	}

	//region setter/getter
	public Optional<ContentGoalExpression> getExpression() {
		return goal.map(ContentGoal::getExpression);
	}

	public Optional<String> getMasterKeyWord() {
		return goal.map(ContentGoal::getMasterKeyword);
	}


	public KnowledgeObject getStructurePoint() {
		return structurePoint;
	}

	public void setStructurePoint(KnowledgeObject structurePoint) {
		this.structurePoint = structurePoint;
	}

	public KnowledgeElement getMainElement() {
		return mainElement;
	}

	public void setMainElement(KnowledgeElement mainElement) {
		this.mainElement = mainElement;
	}

	public KnowledgeElement[] getRelatedElements() {
		return relatedElements;
	}

	public void setRelatedElements(KnowledgeElement[] relatedElements) {
		this.relatedElements = relatedElements;
	}

	public Relation[] getRelations() {
		return relations;
	}

	public void setRelations(Relation[] relations) {
		this.relations = relations;
	}

	public void setGoal(ContentGoal goal) {
		if (goal == null) {
			throw new IllegalArgumentException("goal must not be null");
		}
		this.goal = Optional.of(goal);
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeNode that)) return false;

		if (!Objects.equals(structurePoint, that.structurePoint)) return false;
		if (!Objects.equals(mainElement, that.mainElement)) return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(relatedElements, that.relatedElements)) return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(relations, that.relations);
	}

	@Override
	public int hashCode() {
		int result = structurePoint != null ? structurePoint.hashCode() : 0;
		result = 31 * result + (mainElement != null ? mainElement.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(relatedElements);
		result = 31 * result + Arrays.hashCode(relations);
		return result;
	}

}
