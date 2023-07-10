package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;

import java.util.*;

public abstract class StructureElement {

	private final List<String> alias = new ArrayList<>(); // KnowledgeObject ids
	protected     Relevance    relevance;

	private ContentTarget topic;
	private String        name; // KnowledgeObject id most important alias

	protected StructureElement() {

	}

	protected StructureElement(ContentTarget topic, Relevance relevance, String name,
							   Collection<String> alternatives) {
		this.relevance = relevance;
		this.name = name;
		this.topic = topic;
		alias.addAll(alternatives);
	}

	public boolean addAlias(String alternative) {
		return alias.add(alternative);
	}

	public boolean removeAlias(String alternative) {
		return alias.remove(alternative);
	}

	public boolean moveUpAlias(String alternative) {
		int index = alias.indexOf(alternative);
		if (index > 0) {
			alias.remove(index);
			alias.add(index - 1, alternative);
			return true;
		}
		return false;
	}

	public boolean moveDownAlias(String alternative) {
		int index = alias.indexOf(alternative);
		if (index < alias.size() - 1) {
			alias.remove(index);
			alias.add(index + 1, alternative);
			return true;
		}
		return false;
	}

	public boolean moveAlias(String alternative, int newIndex) {
		int index = alias.indexOf(alternative);
		if (index >= 0 && index < alias.size() && newIndex >= 0 && newIndex < alias.size()) {
			alias.remove(index);
			alias.add(newIndex, alternative);
			return true;
		}
		return false;
	}

	public boolean insertAlias(String alternative, int index) {
		if (index >= 0 && index < alias.size()) {
			alias.add(index, alternative);
			return true;
		}
		return false;
	}

	public boolean hasAlias(String alternative) {
		return alias.contains(alternative);
	}

	public abstract Relevance updateRelevance();


	//region setter/getter
	public Set<String> getAlternatives() {
		return Collections.unmodifiableSet(new LinkedHashSet<>(alias));
	}

	public String getName() {
		return name;
	}

	public Relevance getRelevance() {
		return relevance;
	}

	public ContentTarget getTopic() {
		return topic;
	}

	public void setTopic(ContentTarget topic) {
		this.topic = topic;
	}

	public boolean isValid() {
		return relevance != Relevance.TO_SET;
	}
//endregion


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StructureElement that)) return false;

		if (!alias.equals(that.alias)) return false;
		if (relevance != that.relevance) return false;
		if (!Objects.equals(topic, that.topic)) return false;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		int result = alias.hashCode();
		result = 31 * result + (relevance != null ? relevance.hashCode() : 0);
		result = 31 * result + (topic != null ? topic.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "StructureElement{" +
			   "name='" + name +
			   ", alias=" + alias +
			   ", relevance=" + relevance +
			   ", topic=" + topic + '\'' +
			   '}';
	}

}
