package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.RootStructureElement;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A model of knowledge. It contains structure, all elements, relations and sources.
 */
public class KnowledgeModel {

	private static Logger logger = LoggerFactory.getLogger(KnowledgeModel.class);
	private final Map<Relation, RelationIdPair> unfinishedRelations = new HashMap<>();
	private final Set<KnowledgeSource> sources = new HashSet<>();
	private final KnowledgeStructure structure;
	private final Graph<KnowledgeElement, RelationEdge> graph;
	private String version;
	private String name;

	public KnowledgeModel() {
		this(new RootStructureElement());
	}

	public KnowledgeModel(RootStructureElement root) {
		this(root, "0.0.0", "");
	}

	public KnowledgeModel(RootStructureElement root, String version, String name) {
		this.structure = new KnowledgeStructure(root);
		this.name = name;
		this.graph = new DirectedWeightedPseudograph<>(RelationEdge.class);
		this.version = version;
	}

	/**
	 * Adds a relation to the model.
	 * If the relation is already in the model, nothing happens.
	 *
	 * @param relation the relation to add.
	 * @return true if the relation was added, false if not.
	 * @throws IllegalArgumentException if the relation was null.
	 */
	public boolean addAndLink(Relation relation) throws IllegalArgumentException {
		boolean hasFrom = false;
		boolean hasTo = false;
		if (relation == null) {
			throw new IllegalArgumentException("Relation was null!");
		}
		var fromId = relation.getFromId();
		var fromElement = get(fromId);
		var toId = relation.getToId();
		var toElement = get(toId);
		if (fromElement != null) {
			hasFrom = true;
			relation.setFrom(fromElement);
		}
		if (toElement != null) {
			hasTo = true;
			relation.setTo(toElement);
		}
		if (!(hasFrom && hasTo)) {
			unfinishedRelations.put(relation, new RelationIdPair(fromId, toId));
		} else {
			return link(fromElement, toElement, relation);
		}
		return false;
	}

	/**
	 * Link two elements with the given relation.
	 * <p>
	 * The elements are added to the model if they are not already in it.
	 *
	 * @param elementFrom the element from which the relation goes
	 * @param elementTo   the element to which the relation goes
	 * @param relation    the relation
	 * @return true if both elements were added and linked, false if not. false is also returned if the relation doesn't
	 * contain the elements.
	 * @throws IllegalArgumentException if one of the arguments was null.
	 */
	public boolean addAndLink(KnowledgeElement elementFrom, KnowledgeElement elementTo, Relation relation) {
		if (relation == null || elementFrom == null || elementTo == null) {
			throw new IllegalArgumentException("Relation or element was null!");
		}
		if (!relation.getFromId().equals(elementFrom.getId()) || !relation.getToId().equals(elementTo.getId())) {
			return false;
		}
		addKnowledge(elementFrom);
		addKnowledge(elementTo);
		var type = relation.getType();
		link(elementFrom, elementTo, type);
		return true;
	}

	/**
	 * Adds an element to the model and links it with all relations that are already in the model.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if not
	 */
	public boolean addAndLink(KnowledgeElement element) {
		addKnowledge(element);
		var relations = element.getRelations();
		relations.forEach(relation -> {
			var toId = relation.getToId();
			if (contains(toId)) {
				link(element, get(toId), relation.getType());
			} else {
				unfinishedRelations.put(relation, new RelationIdPair(element.getId(), toId));
			}
		});
		return true;
	}

	/**
	 * Adds an element to the model.  Link the element to all existing elements that are described in relations.
	 * If the element is already in the model, nothing happens.
	 *
	 * @param element the element to add
	 * @return true if the element was added, false if not
	 */
	public boolean addKnowledge(KnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		if (!element.getRelations().isEmpty()) {
			addAndLink(element);
		}
		return graph.addVertex(element);
	}

	/**
	 * Adds a collection of elements to the model. Link all new elements with all elements that are already in the model
	 * and described in the relations.
	 *
	 * @param elements the elements to add.
	 * @return true if at least one element was added, false if not.
	 */
	public boolean addKnowledge(Collection<KnowledgeElement> elements) {
		if (elements.isEmpty()) {
			return false;
		}
		var adding = elements.stream().map(this::addKnowledge).max(Boolean::compareTo).orElseThrow();
		if (hasUnfinishedRelations()) {
			tryCompleteLinking();
		}
		updateStructure();
		return adding;
	}

	public boolean addSource(Collection<KnowledgeSource> sources) {
		return this.sources.addAll(sources);
	}

	public boolean addSource(KnowledgeSource sources) {
		return this.sources.add(sources);
	}

	/**
	 * Adds a structure to the given structure element.
	 *
	 * @param structure the structure to add
	 * @param part      the part to add to the structure
	 * @return true if the structure was added, false if not
	 */
	public boolean addStructureTo(KnowledgeFragment structure, KnowledgeObject part) {
		if (structure == null || part == null) {
			return false;
		}
		if (!getRoot().contains(structure)) {
			return false;
		}
		structure.addObject(part);
		return true;
	}

	public boolean addStructureToRoot(KnowledgeObject object) {
		this.structure.getRoot().addObject(object);
		updateStructure();
		return true;
	}

	public boolean contains(String elementId) throws IllegalArgumentException {
		if (elementId == null) {
			throw new IllegalArgumentException("ElementId was null!");
		}
		return graph.vertexSet().stream().anyMatch(it -> it.getId().equals(elementId));
	}

	public boolean contains(KnowledgeElement element) throws IllegalArgumentException {
		if (element == null) {
			throw new IllegalArgumentException("KnowledgeElement was null!");
		}
		return contains(element.getId());
	}

	public Set<KnowledgeElement> findAll(String elementId) {
		var matchingIDs = findMatchingIDs(elementId);
		return null; // todo
	}

	private Collection<String> findMatchingIDs(String elementId) {
		return getIDs().stream().filter(it -> it.split("-")[0].equals(elementId)).toList();
	}

	public KnowledgeElement get(String id) throws NoSuchElementException {
		return graph.vertexSet().stream().filter(it -> it.getId().equals(id)).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No element with id " + id + " found"));
	}

	/**
	 * Check if there are any relations that could not be completed.
	 *
	 * @return true if there are unfinished relations, false if not.
	 */
	public boolean hasUnfinishedRelations() {
		return !unfinishedRelations.isEmpty();
	}

	public boolean link(KnowledgeElement from, KnowledgeElement to, Relation relation) {
		var edge = new RelationEdge(relation.getType());
		return graph.addEdge(from, to, edge);
	}

	public RelationEdge link(KnowledgeElement from, KnowledgeElement to, RelationType type) throws IllegalStateException {
		var newEdge = new RelationEdge(type);
		if (graph.addEdge(from, to, newEdge)) {
			throw new IllegalStateException();
		}
		return newEdge;
	}

	public boolean link(KnowledgeElement from, KnowledgeElement to, RelationEdge edge) {
		return graph.addEdge(from, to, edge);
	}

	public boolean remove(KnowledgeElement element) {
		return graph.removeVertex(element);
	}

	/**
	 * Tries to complete all relations that were not completed when the elements were added.
	 */
	public void tryCompleteLinking() {
		for (var entry : unfinishedRelations.entrySet()) {
			var relation = entry.getKey();
			var fromId = relation.getFromId();
			var toId = relation.getToId();
			if (contains(fromId) && contains(toId)) {
				link(get(fromId), get(toId), relation.getType());
			}
		}
	}

	private void updateStructure() {
		for (var elem : graph.vertexSet()) {
			var structureId = elem.getStructureId();
			if (structureId == null) {
				continue;
			}
			try {
				structure.getObjectById(structureId).linkElement(elem);
			} catch (NoSuchElementException e) {
				logger.warn("Object {} is not part of the structure", elem.getId());
			}
		}
	}

	//region getter / setter
	public KnowledgeFragment getRoot() {
		return structure.getRoot();
	}

	public List<String> getIDs() {
		return graph.vertexSet().stream().map(KnowledgeElement::getId).toList();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public Set<KnowledgeElement> getElements() {
		return graph.vertexSet();
	}
//endregion

	@Override
	public int hashCode() {
		int result = graph.hashCode();
		result = 31 * result + version.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeModel that)) return false;
		if (!version.equals(that.version)) return false;
		if (!graph.equals(that.graph)) return false;
		return name.equals(that.name);
	}

	private record RelationIdPair(String fromId, String toId) {
	}
}

/**
 * A relation edge between two elements.
 * It contains the type of the relation.
 */
class RelationEdge extends DefaultEdge {
	private final RelationType type;

	public RelationEdge(RelationType type) {
		this.type = type;
	}

	//region getter / setter
	public RelationType getRelation() {
		return type;
	}
//endregion
}