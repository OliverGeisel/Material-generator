package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;


public class CustomRelation extends Relation {

	private final RelationType customType;

	public CustomRelation(String name, RelationType type) {
		super(name, "UNKNOWN", "UNKNOWN", type);
		customType = type;
	}

	public CustomRelation(String name, String fromId, String toId, RelationType type) {
		super(name, fromId, toId, type);
		customType = type;
	}

	//region setter/getter
	@Override
	public RelationType getType() {
		return customType;
	}
//endregion
}
