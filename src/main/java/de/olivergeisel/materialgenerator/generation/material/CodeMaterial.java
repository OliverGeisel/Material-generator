package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CodeMaterial extends Material {

	private String language;
	@Column(length = 100_000)
	private String code;
	private String title;

	public CodeMaterial(String language, String code, KnowledgeElement element) {
		super(MaterialType.WIKI, element.getId(), element.getId(), element.getStructureId());
		this.language = language;
		this.code = code.replace("\n", "<br>").replace("\\\\t", "    ");
	}

	public CodeMaterial(String language, String code, String title, KnowledgeElement element) {
		super(MaterialType.WIKI, title, element.getId(), element.getStructureId());
		this.title = title;
		this.language = language;
		this.code = code.replace("\n", "<br>").replace("\\\\t", "    ");
	}

	protected CodeMaterial() {
		super(MaterialType.WIKI);
	}

	@Override
	public String shortName() {
		return "Code: " + title;
	}

	//region setter/getter
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
//endregion

	@Override
	public String toString() {
		return "CodeMaterial{" +
			   "title='" + title + '\'' +
			   ", code='" + code + '\'' +
			   ", language='" + language + '\'' +
			   '}';
	}
}
