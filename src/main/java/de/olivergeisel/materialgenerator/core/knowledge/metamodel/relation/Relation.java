package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

public abstract class Relation {

	private final RelationType type;
	private final String name;
	private String fromId;
	private String toId;
	private KnowledgeElement from;
	private KnowledgeElement to;

	protected Relation(String name, String fromId, String toId, RelationType type) {
		if (fromId == null || toId == null || type == null || name == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}
		this.type = type;
		this.fromId = fromId;
		this.toId = toId;
		this.name = name;
	}

//region getter / setter
	public String getFromId() {
		return fromId;
	}

	public String getToId() {
		return toId;
	}

	public KnowledgeElement getFrom() {
		return from;
	}

	public void setFrom(KnowledgeElement from) {
		this.from = from;
		fromId = from.getId();
	}

	public String getName() {
		return name;
	}

//region setter/getter
	/**
	 * Get the target of the relation
	 *
	 * @return target of the relation
	 * @throws IllegalStateException if target is not set
	 */
	public KnowledgeElement getTo() throws IllegalStateException {
		if (to == null) {
			throw new IllegalStateException("to is not set");
		}
		return to;
	}

	/**
	 * @param to
	 */
	public void setTo(KnowledgeElement to) {
		if (to == null) {
			throw new IllegalArgumentException("to must not be null");
		}
		if (to.getId() == null || !to.getId().equals(toId)) {
			throw new IllegalArgumentException("to.id must not be null and must match toId");
		}
		this.to = to;
		toId = to.getId();
	}
//endregion

	public RelationType getType() {
		return type;
	}
//endregion

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + fromId.hashCode();
		result = 31 * result + toId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Relation relation)) return false;

		if (!name.equals(relation.name)) return false;
		if (!fromId.equals(relation.fromId)) return false;
		return toId.equals(relation.toId);
	}

	@Override
	public String toString() {
		return "Relation " + name + ": " + fromId + " → " + toId;
	}
}
