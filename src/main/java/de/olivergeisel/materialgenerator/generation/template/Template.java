package de.olivergeisel.materialgenerator.generation.template;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Entity
public abstract class Template {
	protected final TemplateType type;
	@Id
	@Column(name = "id", nullable = false)
	private UUID id;
	private String name;
	private String content;
	private File file;

	protected Template(File file, TemplateType type) {
		this.type = type;
		this.name = file.getName();
		this.file = file;
		readContent();
	}

	private Template() {
		this.type = null;
	}

	protected Template(TemplateType type) {
		this.type = type;
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
		return type;
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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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
