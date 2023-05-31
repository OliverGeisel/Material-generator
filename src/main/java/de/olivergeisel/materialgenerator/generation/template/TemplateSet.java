package de.olivergeisel.materialgenerator.generation.template;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Entity
public class TemplateSet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;
	private String name;

	@ManyToOne
	@JoinColumn(name = "definition_template_id")
	private DefinitionTemplate definitionTemplate;

	@ManyToOne
	private TextTemplate textTemplate;
	@ManyToOne
	private ExampleTemplate exampleTemplate;
	@OneToMany
	private Set<Template> extraTemplates;

	public boolean addAllTemplates(TemplateSet templateSet) {
		return extraTemplates.addAll(templateSet.getExtraTemplates());
	}

	public boolean addTemplate(Template template) {
		return extraTemplates.add(template);
	}

	public boolean removeTemplate(Template template) {
		return extraTemplates.remove(template);
	}

	public boolean supportsTemplate(TemplateType type) {
		if (type == TemplateType.DEFINITION || type == TemplateType.TEXT || type == TemplateType.EXAMPLE)
			return true;
		return extraTemplates.stream().anyMatch(it -> it.getTemplateType().equals(type));
	}

	//region getter / setter
	public TextTemplate getTextTemplate() {
		return textTemplate;
	}

	public void setTextTemplate(TextTemplate textTemplate) {
		this.textTemplate = textTemplate;
	}

	public ExampleTemplate getExampleTemplate() {
		return exampleTemplate;
	}

	public void setExampleTemplate(ExampleTemplate exampleTemplate) {
		this.exampleTemplate = exampleTemplate;
	}

	//
//
	public DefinitionTemplate getDefinitionTemplate() {
		return definitionTemplate;
	}

	public void setDefinitionTemplate(DefinitionTemplate definitionTemplate) {
		this.definitionTemplate = definitionTemplate;
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

	private Set<Template> getExtraTemplates() {
		return Collections.unmodifiableSet(extraTemplates);
	}
//endregion

}
