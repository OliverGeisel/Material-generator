package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class KnowledgeFragment extends KnowledgeObject {

	private final List<KnowledgeObject> elements;
	private String name;

	public KnowledgeFragment(String name) {
		this(name, null);
	}

	public KnowledgeFragment(String name, KnowledgeObject part) {
		super(name);
		elements = new ArrayList<>();
		this.name = name;
		if (part == null) {
			return;
		}
		elements.add(part);
	}

	public boolean addObject(KnowledgeObject object) {
		if (object == null || this == object || elements.contains(object)) {
			return false;
		}
		return elements.add(object);
	}

	public boolean contains(KnowledgeObject object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		for (KnowledgeObject element : elements) {
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

	public boolean contains(String id) throws NoSuchElementException {
		if (id == null) {
			throw new NoSuchElementException("id must not be null");
		}
		if (id.equals(getId())) {
			return true;
		}
		for (KnowledgeObject element : elements) {
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
		for (KnowledgeObject element : elements) {
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
		if (!elements.contains(object)) {
			return false;
		}
		return elements.remove(object);
	}

	//region getter / setter
	//
	public List<KnowledgeObject> getElements() {
		return Collections.unmodifiableList(elements);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//endregion
}
