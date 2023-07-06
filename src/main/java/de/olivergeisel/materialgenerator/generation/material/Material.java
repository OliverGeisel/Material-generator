package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.course.MaterialOrderPart;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A material is a part of a course. It can be a text, an example, a proof, a definition and so on. It contains all
 * values for the specific Materialtype and TemplateInfo. It is used to generate the final material. The TemplateInfo
 * has all information for the template. Specific Materials have more Information about the Material.  @see
 * TemplateInfo for more information.
 * <p>
 * The MaterialType is a general type of the material. It is only a classification from MDTea.
 */
@Entity
public class Material extends MaterialOrderPart {
	/**
	 * The values of the material, which are used in the template
	 */
	@ElementCollection
	@CollectionTable(name = "material_entity_map", joinColumns = @JoinColumn(name = "entity_id"))
	@MapKeyColumn(name = "key_column")
	@Column(name = "value_column")
	private final Map<String, String> values = new HashMap<>();
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

	protected Material() {

	}

	protected Material(MaterialType type) {
		this.type = type;
	}

	protected Material(MaterialType type, TemplateInfo templateInfo) {
		this.type = type;
		this.templateInfo = templateInfo;
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

	protected Material(String term, String termId, String structureId, MaterialType type, TemplateInfo templateInfo) {
		this.term = term;
		this.termId = termId;
		this.structureId = structureId;
		this.type = type;
		this.templateInfo = templateInfo;
	}

	public boolean addValue(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("key and value must not be null");
		}
		return values.put(key, value) != null;
	}

	public boolean removeValue(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		}
		return values.remove(key) != null;
	}

	@Override
	public MaterialOrderPart find(UUID id) {
		if (this.getId().equals(id)) return this;
		return null;
	}

	//region setter/getter

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	@Override
	public boolean isValid() {
		return true;
	}

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

	/**
	 * Set the values of the material. Deletes all existing values and replaces them with the new ones.
	 *
	 * @param values the values to set (must not be null)
	 */
	public void setValues(Map<String, String> values) {
		if (values == null) {
			throw new IllegalArgumentException("values must not be null");
		}
		this.values.clear();
		this.values.putAll(values);
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
		return "Material{" + "term='" + term + '\'' + ", structureId='" + structureId + '\''
				+ ", type=" + type + ", template=" + templateInfo + ", values=" + values + '}';
	}
}
