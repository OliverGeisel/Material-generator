package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.Entity;
import java.io.File;

@Entity
public class TextTemplate extends Template {
	private String term;
	private String text;

	public TextTemplate() {
		super(TemplateType.TEXT);
	}

	public TextTemplate(File file) {
		super(file, TemplateType.TEXT);
	}

	public TextTemplate(String term, String text) {
		super(TemplateType.TEXT);
		this.term = term;
		this.text = text;
	}

	//region getter / setter
	//
//
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
//endregion
}
