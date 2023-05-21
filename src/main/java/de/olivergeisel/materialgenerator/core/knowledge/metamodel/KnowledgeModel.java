package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.*;

public class KnowledgeModel {
	private final List<KnowledgeElement> elements = new LinkedList<>();
	private final Map<String, Set<KnowledgeElement>> unfinishedRelations = new HashMap<>();

	private final Set<KnowledgeSource> sources = new HashSet<>();
	private final KnowledgeObject root;

	private final Graph<KnowledgeElement, DefaultEdge> graph;

	public KnowledgeModel(KnowledgeObject root) {
		this.root = root;
		this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
	}

	public DefaultEdge link(KnowledgeElement element1, KnowledgeElement element2) {
		return graph.addEdge(element1, element2);
	}

	public boolean add(KnowledgeElement element) {
		var result = elements.add(element);
		graph.addVertex(element);
		return result;
	}

	public boolean remove(KnowledgeElement element) {
		var result = elements.remove(element);
		graph.removeVertex(element);
		return result;
	}

	public boolean contains(KnowledgeElement element) {
		return elements.contains(element);
	}

	public boolean contains(String id) {
		return elements.stream().anyMatch(it -> it.getId().equals(id));
	}

	public KnowledgeElement get(String id) {
		return graph.vertexSet().stream().filter(it -> it.getId().equals(id)).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No element with id " + id + " found"));
	}

	public boolean addAndLink(KnowledgeElement element1, KnowledgeElement element2) {
		var b1 = add(element1);
		var b2 = add(element2);
		var edge = link(element1, element2);
		return b1 && b2;
	}

	public boolean addAndLink(KnowledgeElement element) {
		add(element);
		var relations = element.getRelations();
		relations.forEach(it -> {
			var id = it.getElement();
			if (contains(id)) {
				link(element, get(id));
			} else {
				if (unfinishedRelations.containsKey(id)) {
					unfinishedRelations.get(id).add(element);
				} else {
					var set = new HashSet<KnowledgeElement>();
					set.add(element);
					unfinishedRelations.put(it, set);
				}
			}
		});
		return true;
	}


	private Collection<String> findMatchingIDs(String element) {
		return getIDs().stream().filter(it -> it.split("-")[0].equals(element)).toList();
	}

	public void findAll(String element) {
		var matchingIDs = findMatchingIDs(element);


	}

	//
//
	public List<KnowledgeElement> getElements() {
		return elements;
	}

	public List<String> getIDs() {
		return graph.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}
}
