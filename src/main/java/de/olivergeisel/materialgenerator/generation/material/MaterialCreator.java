package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;

import java.util.Map;

public class MaterialCreator {

	public MaterialAndMapping createWikiMaterial(KnowledgeElement mainTerm, String name, TemplateInfo templateInfo,
												 Map<String, String> values, KnowledgeElement... relatedElements) {
		var newMaterial = new Material(MaterialType.WIKI, mainTerm);
		return fillMaterial(mainTerm, name, templateInfo, values, newMaterial, relatedElements);
	}

	public MaterialAndMapping createExampleMaterial(KnowledgeElement mainTerm, String name, TemplateInfo templateInfo,
													Map<String, String> values, KnowledgeElement... relatedElements) {
		var newMaterial = new Material(MaterialType.EXAMPLE, mainTerm);
		return fillMaterial(mainTerm, name, templateInfo, values, newMaterial, relatedElements);
	}

	private MaterialAndMapping fillMaterial(KnowledgeElement mainTerm, String name, TemplateInfo templateInfo, Map<String, String> values, Material newMaterial, KnowledgeElement[] relatedElements) {
		newMaterial.setName(name);
		newMaterial.setTemplateInfo(templateInfo);
		newMaterial.setValues(values);
		MaterialMappingEntry mapping = new MaterialMappingEntry(newMaterial);
		mapping.add(mainTerm);
		mapping.add(relatedElements);
		return new MaterialAndMapping(newMaterial, mapping);
	}
}
