package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;

import javax.persistence.Embeddable;
import java.io.File;

@Embeddable
public class ListTemplate extends TemplateInfo {

	private String list;

	public ListTemplate() {
		super(TemplateType.LIST);
	}

	public ListTemplate(String list) {
		super(TemplateType.LIST);
		this.list = list;
	}

	public ListTemplate(File file) {
		super(file, TemplateType.LIST);
	}

//region getter / setter
	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}
//endregion
}
