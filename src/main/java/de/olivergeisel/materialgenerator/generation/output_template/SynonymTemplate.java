package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.Embeddable;
import java.io.File;

@Embeddable
public class SynonymTemplate extends TemplateInfo {

	private String synonym;

	public SynonymTemplate() {
		super(TemplateType.TEXT);
	}

	public SynonymTemplate(String synonym) {
		super(TemplateType.TEXT);
		this.synonym = synonym;
	}

	public SynonymTemplate(File file) {
		super(file, TemplateType.SYNONYM);
	}

//region getter / setter
	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}
//endregion
}
