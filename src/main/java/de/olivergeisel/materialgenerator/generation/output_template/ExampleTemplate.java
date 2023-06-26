package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class ExampleTemplate extends TemplateInfo {
	private String example;

	public ExampleTemplate(UUID mainTermId, String example) {
		super(TemplateType.EXAMPLE, mainTermId);
		this.example = example;
	}

	public ExampleTemplate(UUID mainTermId) {
		super(TemplateType.EXAMPLE, mainTermId);
	}

	public ExampleTemplate() {
		super(TemplateType.EXAMPLE);
	}

	//region setter/getter
	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
//endregion

}
