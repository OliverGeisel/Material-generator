package de.olivergeisel.materialgenerator.core.knowledge.metamodel.structure;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeFragment extends KnowledgeObject{
	private List<KnowledgeObject> elements;
	private String name;
	public KnowledgeFragment(String name, KnowledgeObject part){
		elements=new ArrayList<>();
		this.name=name;
		elements.add(part);
	}
}
