package de.olivergeisel.materialgenerator.generation.templates.template_infos;

import de.olivergeisel.materialgenerator.generation.templates.TemplateType;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public abstract class BasicTemplate extends TemplateInfo {

	public BasicTemplate(TemplateType type) {
		super(type);
	}

	public BasicTemplate(TemplateType type, UUID mainTermId) {
		super(type, mainTermId);
	}

	public BasicTemplate() {
		super();
	}

}
