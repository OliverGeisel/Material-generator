package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

import static de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.BasicRelation.idFromName;

public class RelationGenerator {

	public static final String UNKNOWN = "UNKNOWN";

	private RelationGenerator() {
		// Utility class
	}

	/**
	 * Creates a new Relation object based on the given parameters.
	 *
	 * @param typeName The name of the RelationType
	 * @return A new Relation object
	 */
	public static Relation create(String typeName) {
		RelationType type;
		try {
			type = RelationType.valueOf(typeName);
		} catch (IllegalArgumentException iae) {
			type = RelationType.CUSTOM;
		}
		return type != RelationType.CUSTOM ?
				new BasicRelation(type, UNKNOWN, UNKNOWN) : new CustomRelation(UNKNOWN, type);
	}

	public static Relation create(String typeName, String fromId, String toId) {
		RelationType type;
		try {
			type = RelationType.valueOf(typeName);
		} catch (IllegalArgumentException iae) {
			type = RelationType.CUSTOM;
		}
		var name = type != RelationType.CUSTOM ? idFromName(type.name(), fromId, toId) : UNKNOWN;
		return type != RelationType.CUSTOM ?
				new BasicRelation(type, fromId, toId) : new CustomRelation(name, fromId, toId, type);
	}
}
