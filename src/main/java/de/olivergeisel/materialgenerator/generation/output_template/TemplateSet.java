package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class TemplateSet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;
	private String name;

	private volatile BasicTemplates basicTemplates = new BasicTemplates();
	@ElementCollection
	private Set<String> extraTemplates; // Todo Not supported yet

	public TemplateSet() {
	}

	public TemplateSet(String name) {
		this.name = name;
	}

	public boolean addAllTemplates(Set<TemplateInfo> templates) {
		return true;//extraTemplates.addAll(templates);
	}

	public boolean addTemplate(TemplateInfo template) {
		return true;//extraTemplates.add(template);
	}

	public boolean removeTemplate(TemplateInfo template) {
		return
				true; //extraTemplates.remove(template);
	}

	public boolean supportsTemplate(TemplateType type) {
		var basics = TemplateType.class.getDeclaredFields();
		for (var field : basics) {
			try {
				if (field.get(null).equals(type))
					return true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return true; //extraTemplates.stream().anyMatch(it -> it.getTemplateType().equals(type));
	}

	//region getter / setter
	private Set<TemplateInfo> getExtraTemplates() {
		return null;// Collections.unmodifiableSet(extraTemplates);
	}

	public TextTemplate getTextTemplate() {
		return basicTemplates.getTextTemplate();
	}

	public void setTextTemplate(TextTemplate textTemplate) {
		basicTemplates.setTextTemplate(textTemplate);
	}

	public ExampleTemplate getExampleTemplate() {
		return basicTemplates.getExampleTemplate();
	}

	public void setExampleTemplate(ExampleTemplate exampleTemplate) {
		basicTemplates.setExampleTemplate(exampleTemplate);
	}

	public DefinitionTemplate getDefinitionTemplate() {
		return basicTemplates.getDefinitionTemplate();
	}

	public void setDefinitionTemplate(DefinitionTemplate definitionTemplate) {
		this.basicTemplates.setDefinitionTemplate(definitionTemplate);
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setListTemplate(ListTemplate listTemplate) {

	}

	public void setSynonymTemplate(SynonymTemplate synonymTemplate) {
		basicTemplates.setSynonymTemplate(synonymTemplate);
	}

	public void setAcronymTemplate(AcronymTemplate acronymTemplate) {
		basicTemplates.setAcronymTemplate(acronymTemplate);
	}
//endregion

}
