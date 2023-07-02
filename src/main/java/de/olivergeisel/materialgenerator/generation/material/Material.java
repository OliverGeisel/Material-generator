package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;

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
	/**
	 * Part in the structure of the knowledge base
	 */
	private String structureId;
	@Enumerated(EnumType.ORDINAL)
	private MaterialType type;
	@ManyToOne(cascade = CascadeType.ALL)
	private TemplateInfo templateInfo;
	@ElementCollection
	@CollectionTable(name = "material_entity_map", joinColumns = @JoinColumn(name = "entity_id"))
	@MapKeyColumn(name = "key_column")
	@Column(name = "value_column")
	private Map<String, String> values;

	protected Material() {

	}

	protected Material(MaterialType type) {
		this.type = type;
	}

	public Material(MaterialType type, String term, String termId, String structureId) {
		this.type = type;
		templateInfo = null;
		this.termId = termId;
		this.term = term;
		this.structureId = structureId;
	}

	public Material(MaterialType type, KnowledgeElement element) {
		this.type = type;
		templateInfo = null;
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		if (element.getType() != KnowledgeType.TERM) {
			throw new IllegalArgumentException("element must be of type TERM");
		}
		this.termId = element.getId();
		this.term = element.getContent();
		this.structureId = element.getStructureId();
	}

	@Override
	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return null;
	}

	//region setter/getter
	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
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

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public MaterialType getType() {
		return type;
	}

	public void setType(MaterialType type) {
		this.type = type;
	}

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
	}
//endregion

	@Override
	public String toString() {
		return "Material{" + "term='" + term + '\'' + ", structureId='" + structureId + '\'' + ", type=" + type + ", template=" + templateInfo + ", values=" + values + '}';
	}
}
