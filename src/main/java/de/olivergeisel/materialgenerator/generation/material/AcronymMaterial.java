package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class AcronymMaterial extends ListMaterial {

	protected AcronymMaterial() {
		super();
	}

	protected AcronymMaterial(MaterialType type, TemplateInfo templateInfo) {
		super(type, templateInfo);
	}

	public AcronymMaterial(List<String> acronyms, boolean numerated, TemplateInfo templateInfo) {
		super(MaterialType.WIKI, templateInfo, "Abkürzung", acronyms, numerated);
	}
}
