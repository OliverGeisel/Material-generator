package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.Embeddable;
import java.io.File;

@Embeddable
public class ExampleTemplate extends TemplateInfo {
	private String term;
	private String example;

	public ExampleTemplate() {
		super(TemplateType.EXAMPLE);
	}

	public ExampleTemplate(String term, String example) {
		super(TemplateType.EXAMPLE);
		this.term = term;
		this.example = example;
	}

	public ExampleTemplate(File file) {
		super(file, TemplateType.EXAMPLE);
	}

	//region getter / setter
	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
//endregion

}
