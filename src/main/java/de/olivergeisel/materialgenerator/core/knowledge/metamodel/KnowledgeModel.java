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

	public boolean add(KnowledgeElement element) {
		var result = elements.add(element);
		test.addVertex(element);
		List<String> ids = getIDs();
		for (String id : element.getRelations()) {
			if (!ids.contains(id)) {
				if (unknownRelations.containsKey(id)) {
					unknownRelations.get(id).add(element);
				} else {
					var newSet = new HashSet<KnowledgeElement>();
					newSet.add(element);
					unknownRelations.put(id, newSet);
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
		return getIDs().stream().filter(it -> it.split("-")[0].equals(element)).toList();
	}

	public void findAll(String element) {
		var matchingIDs = findMatchingIDs(element);


	}

	//
//
	public List<String> getIDs() {
		return test.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}

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
