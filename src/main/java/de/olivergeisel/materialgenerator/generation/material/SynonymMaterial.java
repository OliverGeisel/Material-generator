package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class SynonymMaterial extends ListMaterial {

	protected SynonymMaterial() {
		super();
	}

	protected SynonymMaterial(MaterialType type, TemplateInfo templateInfo) {
		super(type, templateInfo);
	}

	public SynonymMaterial(List<String> synonyms, boolean numerated, TemplateInfo templateInfo) {
		super(MaterialType.WIKI, templateInfo, "Synonyme", synonyms, numerated);
	}
}
