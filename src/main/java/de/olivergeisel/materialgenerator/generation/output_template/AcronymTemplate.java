package de.olivergeisel.materialgenerator.generation.output_template;

import java.io.File;

public class AcronymTemplate extends Template {

	private String acronym;


	public AcronymTemplate() {
		super(TemplateType.TEXT);
	}

	public AcronymTemplate(File file) {
		super(file, TemplateType.TEXT);
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

}
