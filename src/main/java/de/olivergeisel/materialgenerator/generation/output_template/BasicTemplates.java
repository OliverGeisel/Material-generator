package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class BasicTemplates {

	public static final Set<String> TEMPLATES = Set.of("DEFINITION", "TEXT", "EXAMPLE", "ACRONYM", "LIST", "SYNONYM");
	@Transient
	private static BasicTemplates instance;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "definition_template_id")
	private DefinitionTemplate definitionTemplate;

	@ManyToOne
	@JoinColumn(name = "text_template_id")
	private TextTemplate textTemplate;

	@ManyToOne
	@JoinColumn(name = "example_template_id")
	private ExampleTemplate exampleTemplate;

	@ManyToOne
	@JoinColumn(name = "acronym_template_id")
	private AcronymTemplate acronymTemplate;


	@ManyToOne
	@JoinColumn(name = "list_template_id")
	private ListTemplate listTemplate;
	@ManyToOne
	@JoinColumn(name = "synonym_template_id")
	private SynonymTemplate synonymTemplate;

//region setter/getter
	public BasicTemplates getInstance() {
		return instance;
	}

	public UUID getId() {
		return id;
	}
//endregion

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
