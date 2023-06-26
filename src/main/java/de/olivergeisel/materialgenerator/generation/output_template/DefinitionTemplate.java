package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.DefinitionContent;
import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.*;

@Entity
public class DefinitionTemplate extends TemplateInfo {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("definition");
		allFields.add("relatedTerms");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	@Embedded
	@AttributeOverride(name = "definition", column = @Column(name = "definition_definition"))
	@AttributeOverride(name = "relatedTerms", column = @Column(name = "definition_related_term"))
	private DefinitionContent values;

	public DefinitionTemplate(UUID mainTerm) {
		super(TemplateType.DEFINITION, mainTerm);
	}

	public DefinitionTemplate() {
		super(TemplateType.DEFINITION);
	}

	//region setter/getter
	//region getter / setter
	public String getDefinition() {
		return values.getDefinition();
	}

	public void setDefinition(String definition) {
		this.values.setDefinition(definition);
	}

	public void setRelatedTerms(List<String> terms) {
		values.clearRelatedTerms();
		terms.forEach(t -> values.addTermId(UUID.fromString(t)));
	}
//endregion
//endregion

}
