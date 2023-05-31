package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

public class KnowledgeStructure {
	private final RootStructureElement root;

	public KnowledgeStructure() {
		root = new RootStructureElement();
	}

	public KnowledgeStructure(RootStructureElement root) {
		this.root = root;
	}

//region getter / setter
	//
	public RootStructureElement getRoot() {
		return root;
	}
//endregion
//

}
