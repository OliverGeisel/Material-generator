package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class KnowledgeElement {

	/**
	 * The id of the element in the knowledge base. Can differ from the content field if is Type term.
	 */
	private final String id;
	private final KnowledgeType type;
	private final Set<Relation> relations = new HashSet<>();

	/**
	 * The id of the structure point this element belongs to.
	 */
	protected String structureId;
	/**
	 * The content of the element. Meaning depends on the type of the element.
	 */
	private String content;

	protected KnowledgeElement(String content, String id, String type, Collection<Relation> relations) {
		this.content = content;
		this.id = id;
		this.type = KnowledgeType.valueOf(type.toUpperCase());
		this.relations.addAll(relations);
	}

	protected KnowledgeElement(String content, String id, String type) {
		this.content = content;
		this.id = id;
		this.type = KnowledgeType.valueOf(type.toUpperCase());
	}

	public boolean addRelations(Set<Relation> relations) {
		return relations.addAll(relations);
	}

	//region getter / setter
	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}

	public Set<Relation> getRelations() {
		return relations;
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public KnowledgeType getType() {
		return type;
	}
//endregion

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeElement that)) return false;
		return id.equals(that.id);
	}

}
