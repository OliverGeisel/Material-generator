package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.*;

public class KnowledgeModel {

	private final List<KnowledgeElement> elements = new LinkedList<>();
	private final KnowledgeStructure structure = new KnowledgeStructure();
	private final Set<KnowledgeSource> sources = new HashSet<>();
	private final Map<String, String> graph = new HashMap<>();
	private final Map<String, Set<KnowledgeElement>> unknownRelations = new HashMap<>();
	private final Graph<KnowledgeElement, DefaultEdge> test;
	private String version;
	private String name;

	public KnowledgeModel() {
		this.test = new DefaultUndirectedGraph<>(DefaultEdge.class);
	}

	public KnowledgeModel(String version, String name) {
		this.name = name;
		this.test = new DefaultUndirectedGraph<>(DefaultEdge.class);
		this.version = version;
	}

	public boolean addStructure(KnowledgeStructure parsedStructure) {
		// Todo insert to correct place
		if (structure.getRoot().getElements().isEmpty()) {
			structure.getRoot().addObject(parsedStructure.getRoot());
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
		test.addVertex(element);
		List<String> allRelations = getID();
		for (String linkedElement : element.getRelations().stream().map(it -> it.getTo().getId()).toList()) {
			// todo check
			if (!allRelations.contains(linkedElement)) {
				if (unknownRelations.containsKey(linkedElement)) {
					unknownRelations.get(linkedElement).add(element);
				} else {
					var newSet = new HashSet<KnowledgeElement>();
					newSet.add(element);
					unknownRelations.put(linkedElement, newSet);
				}
			}
		}
		return result;
	}

	public boolean addSource(Collection<KnowledgeSource> sources) {
		return this.sources.addAll(sources);
	}

	public boolean addSource(KnowledgeSource sources) {
		return this.sources.add(sources);
	}

	private Collection<String> findMatchingIDs(String element) {
		return getID().stream().filter(it -> it.split("-")[0].equals(element)).toList();
	}

	public void findAll(String element) {
		var matchingIDs = findMatchingIDs(element);
	}

//
	public List<String> getID() {
		return test.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}
//

	public String getName() {
		return name;
	}

	public Graph<KnowledgeElement, DefaultEdge> getTest() {
		return test;
	}

	public String getVersion() {
		return version;
	}
}
