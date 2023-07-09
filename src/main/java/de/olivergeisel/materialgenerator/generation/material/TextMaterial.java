package de.olivergeisel.materialgenerator.generation.material;

import de.olivergeisel.materialgenerator.generation.templates.template_infos.TextTemplate;

import javax.persistence.Entity;

@Entity
public class TextMaterial extends Material {

	private String headline;
	private String text;

	public TextMaterial(String headline, String text, TextTemplate templateInfo) {
		super(MaterialType.WIKI, templateInfo);
		this.headline = headline;
		this.text = text;
	}

	protected TextMaterial() {
		super(MaterialType.WIKI);
	}

//region setter/getter
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
//endregion
}
