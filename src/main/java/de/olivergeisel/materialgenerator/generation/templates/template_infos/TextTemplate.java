package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import javax.persistence.Entity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class TextTemplate extends BasicTemplate {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("text");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	private String text;

	public TextTemplate() {
		super(TemplateType.TEXT);
	}

	public TextTemplate(UUID mainTermId) {
		super(TemplateType.TEXT, mainTermId);
	}

	public TextTemplate(UUID mainTermId, String text) {
		super(TemplateType.TEXT, mainTermId);
		this.text = text;
	}

	//region setter/getter
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
//endregion
}
