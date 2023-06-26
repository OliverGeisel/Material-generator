package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class AcronymTemplate extends TemplateInfo {

	// Todo rethink if its a list of strings
	private String acronym;


	public AcronymTemplate() {
		super(TemplateType.TEXT);
	}

	public AcronymTemplate(UUID mainTermId) {
		super(TemplateType.ACRONYM, mainTermId);
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

}
