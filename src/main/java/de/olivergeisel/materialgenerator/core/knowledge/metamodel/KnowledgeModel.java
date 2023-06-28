package de.olivergeisel.materialgenerator.core.knowledge.metamodel;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.source.KnowledgeSource;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeFragment;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeObject;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.KnowledgeStructure;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure.RootStructureElement;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A model of knowledge. It contains structure, all elements, relations and sources.
 */
public class KnowledgeModel {

	private static final Logger logger = LoggerFactory.getLogger(KnowledgeModel.class);
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
		if (contains(element)) {
			return false;
		}
		var back = graph.addVertex(element);
		if (!element.getRelations().isEmpty()) {
			addAndLink(element);
		}
		return back;
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

	public boolean hasStructureObject(String id) {
		return this.structure.contains(id);
	}

	/**
	 * Check if the model contains the given KnowledgeElement.
	 *
	 * @param elementId
	 * @return
	 * @throws IllegalArgumentException
	 */
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


	/**
	 * Returns all elements that connected with the given element in the model.
	 *
	 * @param elementId the id of the element
	 * @return a set of all elements that are connected with the given element
	 */
	public Set<KnowledgeElement> findAll(String elementId) {
		var matchingIDs = findRelatedElementIDs(elementId);
		return graph.vertexSet().stream().filter(it -> matchingIDs.contains(it.getId())).collect(Collectors.toSet());
	}

	private Collection<String> findRelatedElementIDs(String elementId) {
		var element = get(elementId);
		return Arrays.stream(getRelatedElements(element)).map(KnowledgeElement::getId).collect(Collectors.toList());
	}

	public KnowledgeElement get(String id) throws NoSuchElementException {
		return graph.vertexSet().stream().filter(it -> it.getId().equals(id)).findFirst().orElseThrow(() -> new NoSuchElementException("No element with id " + id + " found"));
	}

	public KnowledgeNode getKnowledgeNode(String targetId) throws NoSuchElementException {
		if (!contains(targetId)) {
			throw new NoSuchElementException("No element with id " + targetId + " found");
		}
		var element = get(targetId);
		var structureObject = structure.getObjectById(element.getStructureId());
		var relatedElements = getRelatedElements(element);
		var relations = getAllRelations(element);

		return new KnowledgeNode(structureObject, element, relatedElements, relations);
	}

	private Relation[] getAllRelations(KnowledgeElement element) {
		var ownRelations = element.getRelations();
		var elementId = element.getId();
		var otherRelations = graph.incomingEdgesOf(element).stream().map(graph::getEdgeSource).map(KnowledgeElement::getRelations).flatMap(it -> it.stream().filter(relation -> relation.getToId().equals(elementId))).toList();
		var returnList = new ArrayList<Relation>();
		returnList.addAll(ownRelations);
		returnList.addAll(otherRelations);
		return returnList.toArray(new Relation[0]);
	}

	private KnowledgeElement[] getRelatedElements(KnowledgeElement element) {
		var outgoing = graph.outgoingEdgesOf(element).stream().map(graph::getEdgeTarget).toList();
		var incoming = graph.incomingEdgesOf(element).stream().map(graph::getEdgeSource).toList();
		var returnList = new ArrayList<KnowledgeElement>();
		returnList.addAll(outgoing);
		returnList.addAll(incoming);
		return returnList.toArray(new KnowledgeElement[0]);
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
		graph.addEdge(from, to, edge);
		graph.addEdge(to, from, new RelationEdge(relation.getType().getInverted(), edge));
		return true;
	}

	public boolean link(KnowledgeElement from, KnowledgeElement to, RelationEdge edge) {
		graph.addEdge(from, to, edge);
		graph.addEdge(to, from, new RelationEdge(edge.getRelation(), edge));
		return true;
	}

	public RelationEdge link(KnowledgeElement from, KnowledgeElement to, RelationType type) throws IllegalStateException {
		var newEdge = new RelationEdge(type);
		if (!contains(from) || !contains(to)) {
			throw new IllegalStateException();
		}
		if (!graph.addEdge(from, to, newEdge)) {
			logger.info("Edge was already linked from {} to {}.", from.getId(), to.getId());
		}
		var reverseEdge = new RelationEdge(type.getInverted(), newEdge);
		if (!graph.addEdge(to, from, reverseEdge)) {
			logger.info("Edge was already linked from {} to {}.", to.getId(), from.getId());
		}
		return newEdge;
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
				logger.warn("StructureObject {} is not part of the structure", structureId);
			}
		}
	}

	public List<KnowledgeNode> getKnowledgeNodesFor(String structureId) {
		if (!hasStructureObject(structureId)) {
			logger.warn("No structure object with id {} found.\nSearch for Object that contains structureName", structureId);
		} else if (!hasStructureSimilar(structureId)) {
			throw new NoSuchElementException("No structure object with id " + structureId + " found");
		} else {
			var similarObject = structure.getRoot().getSimilarObjectById(structureId);
			var elements = similarObject.getLinkedElements();
			var back = new ArrayList<KnowledgeNode>();
			for (var element : elements) {
				back.add(getKnowledgeNode(element.getId()));
			}
			return back;
		}
		var structureObject = structure.getObjectById(structureId);
		var elements = structureObject.getLinkedElements();
		var back = new ArrayList<KnowledgeNode>();
		for (var element : elements) {
			back.add(getKnowledgeNode(element.getId()));
		}
		return back;
	}

	private boolean hasStructureSimilar(String structureId) {
		return structure.containsSimilar(structureId);
	}

	//region setter/getter
	public KnowledgeFragment getRoot() {
		return structure.getRoot();
	}

	/**
	 * Returns a list of all ids of the elements in the model.
	 *
	 * @return a list of all ids of the elements in the model
	 */
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof KnowledgeModel that)) return false;
		if (!version.equals(that.version)) return false;
		if (!graph.equals(that.graph)) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = graph.hashCode();
		result = 31 * result + version.hashCode();
		result = 31 * result + name.hashCode();
		return result;
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
	private RelationEdge inverted;

	/**
	 * Creates a new RelationEdge with the given type and inverted edge.
	 *
	 * @param type     the type of the relation
	 * @param inverted the inverted edge
	 */
	public RelationEdge(RelationType type, RelationEdge inverted) {
		if (inverted == null) {
			throw new IllegalArgumentException("inverted must not be null");
		}
		if (inverted.getInverted().type != type) {
			throw new IllegalArgumentException("inverted must not have an inverted edge");
		}
		inverted.setInverted(this);
		this.inverted = inverted;
		this.type = type;
	}

	/**
	 * Creates a new RelationEdge with the given inverted edge.
	 *
	 * @param inverted the inverted edge
	 */
	public RelationEdge(RelationEdge inverted) throws IllegalArgumentException {
		if (inverted == null) {
			throw new IllegalArgumentException("inverted must not be null");
		}
		inverted.setInverted(this);
		this.inverted = inverted;
		this.type = inverted.getInverted().type;
	}

	/**
	 * Creates a new RelationEdge with the given type.
	 *
	 * @param type the type of the relation
	 */
	public RelationEdge(RelationType type) {
		this.type = type;
	}

	//region setter/getter
	public RelationEdge getInverted() {
		return inverted;
	}

	public void setInverted(RelationEdge inverted) {
		this.inverted = inverted;
	}

	public RelationType getRelation() {
		return type;
	}
//endregion
}