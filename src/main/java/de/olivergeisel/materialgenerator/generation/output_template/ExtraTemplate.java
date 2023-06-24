package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.io.File;
import java.util.Map;

@Embeddable
public class ExtraTemplate extends TemplateInfo {
	@ElementCollection
	Map<String, String> values;

	public ExtraTemplate(File file, TemplateType type) {
		super(file, type);
	}

	protected ExtraTemplate() {

	}
}
