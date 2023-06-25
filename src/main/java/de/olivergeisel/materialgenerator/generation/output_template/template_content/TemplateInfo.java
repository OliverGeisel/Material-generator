package de.olivergeisel.materialgenerator.generation.output_template.template_content;

import de.olivergeisel.materialgenerator.generation.output_template.TemplateType;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public abstract class TemplateInfo {

	public static final Set<String> FIELDS = Set.of("templateType", "mainTermId");
	/**
	 * The type of the template. Specific template in the TemplateSet.
	 */
	@Embedded
	protected final TemplateType templateType;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;
	/**
	 * The name of the template file
	 */
	private UUID mainTermId;

	protected TemplateInfo(TemplateType templateType, UUID mainTermId) {
		this.templateType = templateType;
		this.mainTermId = mainTermId;
	}

	protected TemplateInfo(TemplateType templateType) {
		this.templateType = templateType;
	}

	protected TemplateInfo() {
		this.templateType = null;
	}

	//region getter / setter
	public UUID getId() {
		return id;
	}

	public TemplateType getTemplateType() {
		return templateType;
	}

	public UUID getMainTermId() {
		return mainTermId;
	}

	public void setMainTermId(UUID mainTermId) {
		this.mainTermId = mainTermId;
	}
//endregion


}
