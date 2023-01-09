package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeLeaf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class KnowledgeElement {
	protected KnowledgeLeaf structure;
	private String content;
	private final String id;
	private final KnowlegeType type;

	private final Set<Relation> relations = new HashSet<>();

	protected KnowledgeElement(String content, String id, String type, Collection<Relation> relations) {
		this.content = content;
		this.id = id;
		this.type = KnowlegeType.valueOf(type.toUpperCase());
		this.relations.addAll(relations);
	}

	public KnowledgeLeaf getStructure() {
		return structure;
	}

//
	public Set<Relation> getRelations() {
		return relations;
	}
//

	public void setStructure(KnowledgeLeaf structure) {
		this.structure = structure;
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public KnowlegeType getType() {
		return type;
	}
}
