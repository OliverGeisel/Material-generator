package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

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
		checkArguments(typeName, fromId == null, toId == null);
		RelationType type;
		try {
			type = RelationType.valueOf(typeName.toUpperCase().replace("-", "_"));
		} catch (IllegalArgumentException iae) {
			type = RelationType.CUSTOM;
		}
		var name = type != RelationType.CUSTOM ? idFromName(type, fromId, toId) : typeName;
		return type != RelationType.CUSTOM ?
				new BasicRelation(type, fromId, toId) : new CustomRelation(name, fromId, toId, type);
	}

	public static Relation create(String typeName, KnowledgeElement from, KnowledgeElement to) {
		checkArguments(typeName, from, to);
		RelationType type;
		try {
			type = RelationType.valueOf(typeName.toUpperCase().replace("-", "_"));
		} catch (IllegalArgumentException iae) {
			type = RelationType.CUSTOM;
		}
		var name = type != RelationType.CUSTOM ? idFromName(type, from.getId(), to.getId()) : typeName;
		return type != RelationType.CUSTOM ?
				new BasicRelation(type, from, to) : new CustomRelation(name, from, to, type);
	}

	private static void checkArguments(String typeName, Object from, Object to) throws IllegalArgumentException {
		if (typeName == null) {
			throw new IllegalArgumentException("typeName must not be null");
		}
		if (typeName.isBlank()) {
			throw new IllegalArgumentException("typeName must not be blank");
		}
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to must not be null");
		}
	}
}
