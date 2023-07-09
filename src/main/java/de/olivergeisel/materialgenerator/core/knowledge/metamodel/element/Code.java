package de.olivergeisel.materialgenerator.core.knowledge.metamodel.element;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;

import java.util.Collection;

public class Code extends SimpleElement {

	private String language;
	private String caption;
	private String codeLines;

	public Code(String content, String id, String type) {
		this(content, id, type, null);

	}

	public Code(String content, String id, String type, Collection<Relation> relations) {
		super(content, id, type, relations);
		var elements = content.replace("\\n", "\n").split("\n");
		language = elements[0];
		caption = elements[1];
		var builder = new StringBuilder();
		for (int i = 2; i < elements.length; i++) {
			builder.append(elements[i]);
			builder.append("\n");
		}
		codeLines = builder.toString();
	}

	//region setter/getter
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getCodeLines() {
		return codeLines;
	}

	public void setCodeLines(String codeLines) {
		this.codeLines = codeLines;
	}
//endregion
}
