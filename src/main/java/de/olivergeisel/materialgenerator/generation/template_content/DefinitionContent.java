package de.olivergeisel.materialgenerator.generation.template_content;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public record DefinitionContent(UUID termId, String definition) {
	public DefinitionContent() {
		this(UUID.randomUUID(), "");
	}
}
