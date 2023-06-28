package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

/**
 * Relation types between two {@link de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement KnowledgeElements}
 *
 * @author Oliver Geisel
 * @version 1.1
 * @since 1.0
 */
public enum RelationType {
	/**
	 * A synonym relation between two {@link de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement KnowledgeElements}
	 */
	IS, HAS, USE, IS_SYNONYM_FOR, IS_ACRONYM_FOR, DEFINES, DESCRIBED_AS, EXAMPLE_FOR, PROVEN_BY, CUSTOM,

	// Inverted relations
	CAN_BE, PART_OF, IS_USED_BY, HAS_ACRONYM, HAS_SYNONYM, DEFINED_BY, DESCRIBES, HAS_EXAMPLE, PROOFS;

	//region setter/getter
	//region getter / setter
	public RelationType getInverted() {
		return switch (this) {
			case IS -> RelationType.CAN_BE;
			case HAS -> RelationType.PART_OF;
			case USE -> RelationType.IS_USED_BY;
			case IS_SYNONYM_FOR -> RelationType.HAS_SYNONYM;
			case IS_ACRONYM_FOR -> RelationType.HAS_ACRONYM;
			case DEFINES -> RelationType.DEFINED_BY;
			case DESCRIBED_AS -> RelationType.DESCRIBES;
			case EXAMPLE_FOR -> RelationType.HAS_EXAMPLE;
			case PROVEN_BY -> RelationType.PROOFS;
			// Inverted relations
			case CAN_BE -> RelationType.IS;
			case PART_OF -> RelationType.HAS;
			case IS_USED_BY -> RelationType.USE;
			case HAS_ACRONYM -> RelationType.IS_ACRONYM_FOR;
			case HAS_SYNONYM -> RelationType.IS_SYNONYM_FOR;
			case DEFINED_BY -> RelationType.DEFINES;
			case DESCRIBES -> RelationType.DESCRIBED_AS;
			case HAS_EXAMPLE -> RelationType.EXAMPLE_FOR;
			case PROOFS -> RelationType.PROVEN_BY;
			default -> throw new IllegalStateException("Unexpected value: " + this);
		};
	}
//endregion
//endregion
}
