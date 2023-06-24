package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.Embeddable;
import java.io.File;

@Embeddable
public class AcronymTemplate extends TemplateInfo {

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
