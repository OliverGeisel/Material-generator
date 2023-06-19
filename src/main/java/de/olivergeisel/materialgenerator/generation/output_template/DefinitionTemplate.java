package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.template_content.DefinitionContent;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.io.File;

@Entity
public class DefinitionTemplate extends Template {

	@Embedded
	private DefinitionContent values;

	public DefinitionTemplate(File file) {
		super(file, TemplateType.DEFINITION);
	}

	public DefinitionTemplate() {
		super(TemplateType.DEFINITION);
	}

	public void setValues(DefinitionContent values) {
		this.values = values;
	}

	//region getter / setter
	//
	public String getDefinition() {
		return values.definition();
	}

	public void setDefinition(String definition) {
		this.values = new DefinitionContent(this.values.term(), definition);

	}

	public String getTerm() {
		return values.term();
	}

	public void setTerm(String term) {
		values = new DefinitionContent(term, values.term());
	}
//endregion
//
}
