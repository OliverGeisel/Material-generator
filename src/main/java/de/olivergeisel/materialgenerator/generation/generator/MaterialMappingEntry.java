package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

import java.util.Collections;
import java.util.Set;

public class MaterialMappingEntry {
	private Set<KnowledgeElement> relatedElements;
	private Material material;

	public MaterialMappingEntry(Material material) {
		this.material = material;
	}

	public boolean add(KnowledgeElement... elements) {
		return Collections.addAll(relatedElements, elements);
	}

	public boolean remove(KnowledgeElement... elements) {
		for (var elem : elements) {
			relatedElements.remove(elem);
		}
		return true;
	}
}
