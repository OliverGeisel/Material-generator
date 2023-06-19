package de.olivergeisel.materialgenerator.generation.template_content;

import javax.persistence.Embeddable;

@Embeddable
public record DefinitionContent(String term, String definition) {
	public DefinitionContent() {
		this("", "");
	}
}
