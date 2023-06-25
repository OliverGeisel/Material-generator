package de.olivergeisel.materialgenerator.generation.output_template.template_content;

import de.olivergeisel.materialgenerator.generation.output_template.TemplateType;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.*;

@Entity
public class DefinitionContent extends TemplateInfo {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("definition");
		allFields.add("relatedTerms");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	String definition;
	@ElementCollection
	private List<UUID> relatedTerms = new LinkedList<>();
	public DefinitionContent(UUID mainTermId, String definition) {
		super(TemplateType.DEFINITION, mainTermId);
		this.definition = definition;
	}

	public boolean addTermId(UUID id) {
		return relatedTerms.add(id);
	}

}
