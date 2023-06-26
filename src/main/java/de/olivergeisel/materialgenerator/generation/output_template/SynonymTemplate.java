package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.Entity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class SynonymTemplate extends TemplateInfo {

	public static final Set<String> FIELDS;

	static {
		var allFields = new HashSet<>(TemplateInfo.FIELDS);
		allFields.add("synonym");
		FIELDS = Collections.unmodifiableSet(allFields);
	}

	//todo rethink if lis of synonyms is better

	private String synonym;

	public SynonymTemplate() {
		super(TemplateType.SYNONYM);
	}


	public SynonymTemplate(UUID maiTermId, String synonym) {
		super(TemplateType.SYNONYM, maiTermId);
		this.synonym = synonym;
	}


	//region setter/getter
	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}
//endregion

}
