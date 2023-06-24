package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class BasicTemplates {
	@Embedded
	@AttributeOverride(name = "values.definition", column = @Column(name = "definition_definition"))
	@AttributeOverride(name = "values.termId", column = @Column(name = "definition_term"))
	private DefinitionTemplate definitionTemplate;
	@Embedded
	@AttributeOverride(name = "term", column = @Column(name = "text_term"))
	@AttributeOverride(name = "text", column = @Column(name = "text_text"))
	private TextTemplate textTemplate;
	@Embedded
	@AttributeOverride(name = "term", column = @Column(name = "example_term"))
	@AttributeOverride(name = "example", column = @Column(name = "example_example"))
	private ExampleTemplate exampleTemplate;
	@Embedded
	private AcronymTemplate acronymTemplate;
	@Embedded

	private ListTemplate listTemplate;
	@Embedded
	private SynonymTemplate synonymTemplate;

	public BasicTemplates() {
		this.definitionTemplate = new DefinitionTemplate();
		this.textTemplate = new TextTemplate();
		this.exampleTemplate = new ExampleTemplate();
		this.acronymTemplate = new AcronymTemplate();
		this.listTemplate = new ListTemplate();
		this.synonymTemplate = new SynonymTemplate();
	}

	//region getter / setter
	public DefinitionTemplate getDefinitionTemplate() {
		return definitionTemplate;
	}

	public void setDefinitionTemplate(DefinitionTemplate definitionTemplate) {
		this.definitionTemplate = definitionTemplate;
	}

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

	public AcronymTemplate getAcronymTemplate() {
		return acronymTemplate;
	}

	public void setAcronymTemplate(AcronymTemplate acronymTemplate) {
		this.acronymTemplate = acronymTemplate;
	}

	public ListTemplate getListTemplate() {
		return listTemplate;
	}

	public void setListTemplate(ListTemplate listTemplate) {
		this.listTemplate = listTemplate;
	}

	public SynonymTemplate getSynonymTemplate() {
		return synonymTemplate;
	}

	public void setSynonymTemplate(SynonymTemplate synonymTemplate) {
		this.synonymTemplate = synonymTemplate;
	}
//endregion
}
