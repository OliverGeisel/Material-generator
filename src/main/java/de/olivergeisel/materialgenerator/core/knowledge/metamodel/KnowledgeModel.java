package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.RootStructureElement;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.*;

public class KnowledgeModel {
	private final List<KnowledgeElement> elements = new LinkedList<>();
	private final Map<Relation, Set<KnowledgeElement>> unfinishedRelations = new HashMap<>();
	private final Set<KnowledgeSource> sources = new HashSet<>();
	private final KnowledgeFragment root;

	private final Graph<KnowledgeElement, DefaultEdge> graph;
	private String version;
	private String name;

	public KnowledgeModel() {
		this.root = new RootStructureElement();
		this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
	}

	public KnowledgeModel(KnowledgeFragment root) {
		this.root = root;
		this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
	}

	public KnowledgeModel(KnowledgeFragment root, String version, String name) {
		this.root = root;
		this.name = name;
		this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
		this.version = version;
	}

	public DefaultEdge link(KnowledgeElement element1, KnowledgeElement element2) {
		return graph.addEdge(element1, element2);
	}

	public boolean addStructure(KnowledgeStructure parsedStructure) {
		// Todo insert to correct place
		if (root.getElements().isEmpty()) {
			root.addObject(parsedStructure.getRoot());
		}
		return true;
	}

	public boolean add(Collection<KnowledgeElement> elements) {
		if (elements.isEmpty()) {
			return false;
		}
		return elements.stream().map(this::add).max(Boolean::compareTo).orElseThrow();
	}

	public boolean add(KnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		var result = elements.add(element);
		graph.addVertex(element);
		return result;
	}

	public boolean addSource(Collection<KnowledgeSource> sources) {
		return this.sources.addAll(sources);
	}

	public boolean addSource(KnowledgeSource sources) {
		return this.sources.add(sources);
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
			var relationName = it.getName();
			if (contains(relationName)) {
				link(element, get(relationName));
			} else {
				if (unfinishedRelations.containsKey(it)) {
					unfinishedRelations.get(it).add(element);
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

	public Set<KnowledgeElement> findAll(String element) {
		var matchingIDs = findMatchingIDs(element);
		return null; // todo
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
	public List<KnowledgeElement> getElements() {
		return elements;
	}

	public List<String> getIDs() {
		return graph.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}
}
