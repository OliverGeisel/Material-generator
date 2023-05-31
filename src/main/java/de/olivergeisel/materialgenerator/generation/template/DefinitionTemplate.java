package de.olivergeisel.materialgenerator.generation.template;

import javax.persistence.Entity;
import java.io.File;

@Entity
public class DefinitionTemplate extends Template {

	private String term;
	private String definition;

	public DefinitionTemplate(File file) {
		super(file, TemplateType.DEFINITION);
	}

	public DefinitionTemplate() {
		super(TemplateType.DEFINITION);
	}

	//region getter / setter
	//
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
//endregion
//
}
