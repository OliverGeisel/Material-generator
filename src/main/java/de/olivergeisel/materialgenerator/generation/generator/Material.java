package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.generation.output_template.Template;

import java.util.Map;
import java.util.UUID;

public class Material {

	private final String id;
	private String term;
	private String termId;
	private MaterialType type;
	private Template template;

	public Material(MaterialType type, String term, String termId) {
		this.type = type;
		id = UUID.randomUUID().toString();
		template = null;
		this.termId = termId;
		this.term = term;
	}

	public Material(MaterialType type, KnowledgeElement element) {
		this.type = type;
		id = UUID.randomUUID().toString();
		template = null;
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		if (element.getType() != KnowledgeType.TERM) {
			throw new IllegalArgumentException("element must be of type TERM");
		}
		this.termId = element.getId();
		this.term = element.getContent();
	}

	public String getId() {
		return id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	private Map<String, String> values;

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public Map<String, String> getValues() {
		return values;
	}

	//region getter / setter

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public MaterialType getType() {
		return type;
	}

	public void setType(MaterialType type) {
		this.type = type;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
//endregion
}
