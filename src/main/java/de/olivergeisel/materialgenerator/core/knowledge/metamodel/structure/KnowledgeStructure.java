package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

import java.util.NoSuchElementException;

public class KnowledgeStructure {
	private final RootStructureElement root;

	public KnowledgeStructure() {
		root = new RootStructureElement();
	}

	public KnowledgeStructure(RootStructureElement root) {
		this.root = root;
	}

	public boolean addObjectToRoot(KnowledgeObject object) {
		if (object == null || root == object || root.contains(object)) {
			return false;
		}
		return root.addObject(object);
	}

	public boolean contains(KnowledgeObject object) {
		if (object == null) {
			return false;
		}
		if (object == root) {
			return true;
		}
		return root.contains(object);
	}

	public boolean contains(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		return root.contains(id);
	}

	public KnowledgeObject getObjectById(String id) throws NoSuchElementException {
		return root.getObjectById(id);
	}

	//region getter / setter
	//
	public RootStructureElement getRoot() {
		return root;
	}
//endregion
//

}
