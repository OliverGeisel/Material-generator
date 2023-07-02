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
		return create(typeName, UNKNOWN, UNKNOWN);
	}

	public static Relation create(String typeName, String fromId, String toId) {
		if (typeName == null) {
			throw new IllegalArgumentException("typeName must not be null");
		}
		if (typeName.isBlank()) {
			throw new IllegalArgumentException("typeName must not be blank");
		}
		if (fromId == null || toId == null) {
			throw new IllegalArgumentException("fromId and toId must not be null");
		}
		RelationType type;
		try {
			type = RelationType.valueOf(typeName.toUpperCase().replace("-", "_"));
		} catch (IllegalArgumentException iae) {
			type = RelationType.CUSTOM;
		}
		var name = type != RelationType.CUSTOM ? idFromName(type.name(), fromId, toId) : UNKNOWN;
		return type != RelationType.CUSTOM ?
				new BasicRelation(type, fromId, toId) : new CustomRelation(name, fromId, toId, type);
	}
}
