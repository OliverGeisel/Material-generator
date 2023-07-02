package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

import java.util.*;

public class KnowledgeFragment extends KnowledgeObject {

	private final List<KnowledgeObject> children = new ArrayList<>();
	private String name;

	public KnowledgeFragment(String name) {
		this(name, null);
	}

	public KnowledgeFragment(String name, KnowledgeObject part) {
		super(name);
		this.name = name;
		if (part == null) {
			return;
		}
		children.add(part);
	}

	public boolean containsSimilar(String structureId) {
		if (structureId == null) {
			return false;
		}
		if (this.getId().contains(structureId)) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.containsSimilar(structureId)) {
				return true;
			} else {
				if (element.getId().contains(structureId)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean addObject(KnowledgeObject object) {
		if (object == null || this == object || children.contains(object)) {
			return false;
		}
		return children.add(object);
	}

	public boolean contains(KnowledgeObject object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.contains(object)) {
				return true;
			} else {
				if (element.equals(object)) {
					return true;
				}
			}
		}
		return false;
	}

	public KnowledgeObject getSimilarObjectById(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getId())) {
			return this;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment) {
				KnowledgeObject object = fragment.getSimilarObjectById(id);
				if (object != null) {
					return object;
				}
			} else {
				if (element.getId().contains(id)) {
					return element;
				}
			}
		}
		throw new NoSuchElementException("No element with id " + id + " found");
	}

	public boolean contains(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getId())) {
			return true;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment && fragment.contains(id)) {
				return true;
			} else {
				if (element.getId().equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public KnowledgeObject getObjectById(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getId())) {
			return this;
		}
		for (KnowledgeObject element : children) {
			if (element instanceof KnowledgeFragment fragment) {
				KnowledgeObject object = fragment.getObjectById(id);
				if (object != null) {
					return object;
				}
			} else {
				if (element.getId().equals(id)) {
					return element;
				}
			}
		}
		throw new NoSuchElementException("No element with id " + id + " found");
	}

	public boolean removeObject(KnowledgeObject object) {
		if (!children.contains(object)) {
			return false;
		}
		return children.remove(object);
	}

	//region setter/getter
	@Override
	public Set<KnowledgeElement> getLinkedElements() {
		var ownElements = super.getLinkedElements();
		var back = new HashSet<>(ownElements);
		for (KnowledgeObject child : children) {
			back.addAll(child.getLinkedElements());
		}
		return back;
	}

	public List<KnowledgeObject> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//endregion
}
