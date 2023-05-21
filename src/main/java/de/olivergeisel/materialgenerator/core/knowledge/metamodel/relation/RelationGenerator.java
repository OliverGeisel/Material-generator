package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

public class RelationGenerator {
	public static Relation create(String typeName, String id) {
		RelationType type;
		try {
			type = RelationType.valueOf(typeName);
		} catch (IllegalArgumentException iae) {
			type = RelationType.CUSTOM;
		}
		return type != RelationType.CUSTOM ?
				new BasicRelation(type, "UNKNOWN", "UNKNOWN") : new CustomRelation(id, type);
	}
}
