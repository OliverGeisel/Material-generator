package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;


public class BasicRelation extends Relation {
	private final RelationType type;

	public BasicRelation(RelationType type, String from, String to) {
		super(type.name(), from, to);
		this.type = type;
	}

	public RelationType getType() {
		return type;
	}

}
