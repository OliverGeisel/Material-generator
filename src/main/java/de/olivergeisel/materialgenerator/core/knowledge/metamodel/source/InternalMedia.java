package de.olivergeisel.materialgenerator.core.knowledge.metamodel.source;

public class InternalMedia extends KnowledgeSource {

	private String content;

	public InternalMedia(String id, String name) {
		super(id, name);
	}

//
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
//
}
