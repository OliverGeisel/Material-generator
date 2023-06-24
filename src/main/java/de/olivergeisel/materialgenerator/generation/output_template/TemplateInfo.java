package de.olivergeisel.materialgenerator.generation.output_template;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Embeddable
public abstract class TemplateInfo {

	/**
	 * The type of the template. Specific template in the TemplateSet.
	 */
	@AttributeOverrides(
			{
					@AttributeOverride(name = "type", column = @Column(name = "template_type"))
			}
	)
	protected final TemplateType templateType;
	/**
	 * The name of the template file
	 */
	private String name;

	/**
	 * The content of the template file
	 */
	@Column(length = 100000)
	private String content;
	private File file;

	protected TemplateInfo(File file, TemplateType templateType) {
		this.templateType = templateType;
		if (file == null) {
			throw new IllegalArgumentException("file must not be null");
		}
		this.name = file.getName();
		this.file = file;
		if (file.exists()) {
			readContent();
		}
	}

	protected TemplateInfo(TemplateType templateType) {
		this.templateType = templateType;
	}

	protected TemplateInfo() {
		this.templateType = null;
	}

	public void readContent() {
		try (InputStream inputStream = new FileInputStream(file)) {
			content = new String(inputStream.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//region getter / setter
	public TemplateType getTemplateType() {
		return templateType;
	}

	//
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//endregion
//
}
