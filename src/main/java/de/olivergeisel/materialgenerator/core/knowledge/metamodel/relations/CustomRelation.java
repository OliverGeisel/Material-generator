package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relations;

public class CustomRelation extends Relation {

	private final String customeType;

	protected CustomRelation(String name, String type) {
		super(name);
		customeType = type;
	}

	public String getType() {
		return customeType;
	}
}
