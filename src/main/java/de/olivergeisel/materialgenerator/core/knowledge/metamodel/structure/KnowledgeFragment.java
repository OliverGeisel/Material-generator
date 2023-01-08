package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		if (object == null || elements.contains(object)) {
			return false;
		}
		return elements.add(object);
	}

	public boolean removeObject(KnowledgeObject object) {
		if (!elements.contains(object)) {
			return false;
		}
		return elements.remove(object);
	}

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
}
