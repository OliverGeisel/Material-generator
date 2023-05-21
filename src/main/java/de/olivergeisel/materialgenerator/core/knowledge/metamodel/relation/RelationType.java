package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

/**
 * Relation types between two {@link de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement KnowledgeElements}
 *
 * @author Oliver Geisel
 * @version 1.0
 * @since 1.0
 */
public enum RelationType {
	/**
	 * A synonym relation between two {@link de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement KnowledgeElements}
	 */
	IS,
	HAS,
	USE,
	SYNONYM,
	ACRONYM,
	DEFINED_AS,
	DESCRIBED_AS,
	EXAMPLE_FOR,
	PROVEN_BY,
}

/**
 * Inverted relation types to {@link de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType RelationType}
 *
 * @author Oliver Geisel
 * @version 1.0
 * @see de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType
 * @since 1.0
 */
enum ReverseRelationType {
	/**
	 * A synonym relation between two {@link de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement KnowledgeElements}
	 */
	CAN_BE,
	PART_OF,
	IS_USED_BY,

}