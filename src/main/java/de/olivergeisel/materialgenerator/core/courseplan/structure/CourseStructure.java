package de.olivergeisel.materialgenerator.core.courseplan.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseStructure {
	private final List<StructureChapter> order = new ArrayList<>();

	@Override
	public String toString() {
		return "CourseStructure{" +
				"chapters=" + order.size() +
				'}';
	}

	public boolean add(StructureChapter element) {
		return order.add(element);
	}

//
	public List<StructureChapter> getOrder() {
		return Collections.unmodifiableList(order);
	}
//
}
