package de.olivergeisel.materialgenerator.core.courseplan.structure;

import java.util.Collections;
import java.util.List;

public class CourseStructure {
	private List<StructureElement> order;

	public List<StructureElement> getOrder() {
		return Collections.unmodifiableList(order);
	}


	public boolean add(StructureElement element){
		return order.add(element);
	}
}
