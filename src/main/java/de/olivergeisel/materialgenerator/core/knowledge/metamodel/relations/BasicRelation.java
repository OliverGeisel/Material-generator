package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations;

public class BasicRelation extends Relation{
	public RelationType getType() {
		return type;
	}

	private final RelationType type;

	public BasicRelation(RelationType type) {
		super(type.name());
		this.type = type;
	}

}
