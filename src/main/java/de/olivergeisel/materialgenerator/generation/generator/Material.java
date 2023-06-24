package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateInfo;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@Entity
public class Material extends MaterialOrderPart {

	/**
	 * The term name of the material, which is used in the template
	 */
	private String term;
	/**
	 * The unique term id of the material, which is used in the template
	 */
	private String termId;
	@Enumerated(EnumType.ORDINAL)
	private MaterialType type;
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "templateType", column = @Column(name = "template_type")),
			@AttributeOverride(name = "file", column = @Column(name = "template_file")),
			@AttributeOverride(name = "name", column = @Column(name = "template_name")),
			@AttributeOverride(name = "content", column = @Column(name = "template_content"))
	})
	private TemplateInfo template;

	public Material(MaterialType type, String term, String termId) {
		this.type = type;
		template = null;
		this.termId = termId;
		this.term = term;
	}

	public Material(MaterialType type, KnowledgeElement element) {
		this.type = type;
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
	@ElementCollection
	private Map<String, String> values;

	protected Material() {

	}


	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public Object find(UUID id) {
		if (this.getId().equals(id)) return this;
		return null;
	}

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

	public TemplateInfo getTemplate() {
		return template;
	}

	public void setTemplate(TemplateInfo template) {
		this.template = template;
	}
//endregion
}
