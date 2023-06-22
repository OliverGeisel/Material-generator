package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureGroup extends StructureElementPart {

	private final List<StructureElementPart> parts;

	public StructureGroup(ContentTarget topic, Relevance relevance, String name, Set<KnowledgeObject> alternatives) {
		super(topic, relevance, name, alternatives);
		parts = new ArrayList<>();
	}

	public List<StructureElementPart> getParts() {
		return parts;
	}

	@Override
	public String toString() {
		return "StructureGroup{" +
				"name=" + getName() +
				", parts=" + parts +
				", relevance=" + relevance +
				'}';
	}

	@Override
	public void updateRelevance() {
		relevance = parts.stream().map(StructureElement::getRelevance).max(Enum::compareTo).orElseThrow();
	}


	public boolean add(StructureElementPart element) throws IllegalArgumentException {
		if (element == this || contains(element)) {
			return false;
		}
		return parts.add(element);
	}

	public boolean contains(StructureElementPart element) {
		for (StructureElement part : parts) {
			if (part instanceof StructureGroup group && group.contains(element))
				return true;
			else {
				if (part.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

	public int size() {
		int back = 0;
		for (StructureElement element : parts) {
			if (element instanceof StructureGroup group) {
				back += group.size();
			} else {
				back++;
			}
		}
		return back;
	}
}
