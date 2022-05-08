package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.*;

public class KnowledgeModel {
	private final List<KnowledgeElement> elements = new LinkedList<>();
	private final Map<String, String> graph = new HashMap<>();
	private final Map<String, Set<KnowledgeElement>> unknownRelations = new HashMap<>();

	private Graph<KnowledgeElement, DefaultEdge> test;

	public KnowledgeModel() {
		this.test = new DefaultUndirectedGraph<>(DefaultEdge.class);
	}
	public List<String> getIDs(){
		return test.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}

	public boolean add(KnowledgeElement element) {
		var result = elements.add(element);
		test.addVertex(element);
		 List<String> ids = getIDs();
		 for(String id: element.getRelations()){
			 if (!ids.contains(id)){
				 if(unknownRelations.containsKey(id)){
					 unknownRelations.get(id).add(element);
				 }
				 else {
					 var newSet = new HashSet<KnowledgeElement>();
					 newSet.add(element);
					 unknownRelations.put(id,newSet);
				 }
			 }
		 }
		return result;
	}

	private Collection<String> findMatchingIDs(String element){
		return getIDs().stream().filter(it->it.split("-")[0].equals(element)).toList();
	}
	public void findAll(String element) {
		var matchingIDs = findMatchingIDs(element);


	}
}
