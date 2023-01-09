package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations;

public class CustomRelation extends Relation {

	private final RelationType customType;

	public CustomRelation(String name, RelationType type) {
		super(name);
		customType = type;
	}

//
	public RelationType getType() {
		return customType;
	}
//
}
