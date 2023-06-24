package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.template_content.DefinitionContent;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.File;
import java.util.UUID;

@Embeddable
public class DefinitionTemplate extends TemplateInfo {

	@Embedded
	@AttributeOverride(name = "definition", column = @Column(name = "definition_definition"))
	@AttributeOverride(name = "termId", column = @Column(name = "definition_term"))
	private DefinitionContent values;

	public DefinitionTemplate(File file) {
		super(file, TemplateType.DEFINITION);
	}

	public DefinitionTemplate() {
		super(TemplateType.DEFINITION);
	}

	//region getter / setter
	public String getDefinition() {
		return values.definition();
	}
	public UUID getTerm() {
		return values.termId();
	}

	public void setTerm(UUID termId) {
		values = new DefinitionContent(termId, values.definition());
	}

	public void setDefinition(String definition) {
		this.values = new DefinitionContent(this.values.termId(), definition);

	}

	public void setValues(DefinitionContent values) {
		this.values = values;
	}
//endregion

}
