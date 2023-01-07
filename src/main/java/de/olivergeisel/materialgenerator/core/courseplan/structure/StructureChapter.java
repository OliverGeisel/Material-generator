package de.olivergeisel.materialgenerator.core.courseplan.structure;

import de.olivergeisel.materialgenerator.core.courseplan.Relevance;

import java.util.ArrayList;
import java.util.List;

public class StructureChapter extends StructureElement {


	private final List<StructureElement> parts;
	private double weight;

	public StructureChapter(Relevance relevance, String name, double weight) {
		super(relevance, name);
		this.weight = weight;
		parts = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "StructureChapter{" +
				"name=" + getName() +
				", parts=" + parts.size() +
				", weight=" + weight +
				", relevance=" + relevance +
				'}';
	}

	@Override
	public void updateRelevance() {
		relevance = parts.stream().map(StructureElement::getRelevance).max(Enum::compareTo).orElseThrow();
	}

	public boolean add(StructureElementPart element) throws IllegalArgumentException {
		if (contains(element)) {
			return false;
		}
		return parts.add(element);
	}

	public boolean contains(StructureElementPart element) {
		for (StructureElement element1 : parts) {
			if (element1 instanceof StructureGroup group && group.contains(element))
				return true;
			else {
				if (element1.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

//
	public double getWeight() {
		return weight;
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

	public void setWeight(int weight) {
		this.weight = weight;
	}
//


}
