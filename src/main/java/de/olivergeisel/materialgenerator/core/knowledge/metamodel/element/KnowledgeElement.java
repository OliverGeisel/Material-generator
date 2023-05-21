package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeLeaf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class KnowledgeElement {
	public static class KnowledgeElementRelation {
		private final String elementId;
		private final RelationType relation;

		public KnowledgeElementRelation(RelationType type, String elementId) {
			this.elementId = elementId;
			this.relation = type;
		}

		public String getElement() {
			return elementId;
		}

		public RelationType getRelation() {
			return relation;
		}
	}

	protected KnowledgeLeaf structure;
	private String content;
	private final String id;
	private final KnowledgeType type;

	private final Set<KnowledgeElementRelation> relations = new HashSet<>();

	protected KnowledgeElement(String content, String id, String type, Collection<KnowledgeElementRelation> relations) {
		this.content = content;
		this.id = id;
		this.type = KnowledgeType.valueOf(type.toUpperCase());
		this.relations.addAll(relations);
	}

	public KnowledgeLeaf getStructure() {
		return structure;
	}

	public Set<KnowledgeElementRelation> getRelations() {
		return relations;
	}

	public void setStructure(KnowledgeLeaf structure) {
		this.structure = structure;
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
}
