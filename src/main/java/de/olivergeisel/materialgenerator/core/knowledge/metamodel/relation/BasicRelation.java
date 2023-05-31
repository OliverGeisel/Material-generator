package de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation;


public class BasicRelation extends Relation {

	public BasicRelation(RelationType type, String from, String to) {
		super(idFromName(type.name(), from, to), from, to, type);
	}

	static String idFromName(String type, String from, String to) {
		return type.toUpperCase() + "_" + from + "_" + to;
	}

}
