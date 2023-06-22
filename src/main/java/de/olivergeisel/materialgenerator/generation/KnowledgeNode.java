package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

public class KnowledgeNode {

	private KnowledgeObject structurePoint;
	private KnowledgeElement mainElement;
	private KnowledgeElement[] relatedElements;
	private Relation[] relations;

	public KnowledgeNode(KnowledgeObject structurePoint, KnowledgeElement mainElement, KnowledgeElement[] relatedElements, Relation[] relations) {
		this.structurePoint = structurePoint;
		this.mainElement = mainElement;
		this.relatedElements = relatedElements;
		this.relations = relations;
	}

	//region getter / setter
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
//endregion

}
