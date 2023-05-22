package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;


public class RootStructureElement extends KnowledgeFragment {

	private String key;

	public RootStructureElement() {
		super("Knowledge-Root", null);
	}

//
	public void setKey(String key) {
		this.key = key;
	}
//
}
