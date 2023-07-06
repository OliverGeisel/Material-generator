package de.olivergeisel.materialgenerator.generation.material;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CodeMaterial extends Material {

	private String language;
	@Column(length = 10000)
	private String code;

	public CodeMaterial(String language, String code) {
		super(MaterialType.WIKI);
		this.language = language;
		this.code = code;
	}

	protected CodeMaterial() {
		super(MaterialType.WIKI);
	}

//region setter/getter
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

}
