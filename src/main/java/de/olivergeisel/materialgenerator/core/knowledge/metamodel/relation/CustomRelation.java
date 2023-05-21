package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;


public class CustomRelation extends Relation {

	private final RelationType customType;

	public CustomRelation(String name, RelationType type) {
		super(name, "UNKNOWN", "UNKNOWN");
		customType = type;
	}


	public RelationType getType() {
		return customType;
	}
}
