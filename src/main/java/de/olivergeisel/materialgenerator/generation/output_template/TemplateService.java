package de.olivergeisel.materialgenerator.generation.output_template;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class TemplateService {

	public static final String TEMPLATE_SET_FROM_TEMPLATES_FOLDER = "../templateSets/";
	private final String TEMPLATE_SET_PATH = "src/main/resources/templateSets/";

	public TemplateSet createTemplateSet(String dir, TemplateSet templateSet) {
		File newFolder = new File(TEMPLATE_SET_PATH + dir);
		if (newFolder.exists()) {
			throw new IllegalArgumentException("TemplateSet with name " + dir + " already exists");
		}
		newFolder.mkdir();
		return null;
	}

	public boolean deleteTemplateSet(String id) {
		File dir = new File(TEMPLATE_SET_PATH + id);
		if (dir.exists()) {
			try {
				Files.delete(dir.toPath());
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	public TemplateSet getTemplateSet(String name) {
		File dir = new File(TEMPLATE_SET_PATH + name);
		if (dir.exists()) {
			return loadTemplateSet(dir);
		}
		return null;
	}

	private Template loadTemplate(File file) {
		var typeString = file.getName().replace(".html", "").toUpperCase();
		TemplateType type = TemplateType.valueOf(typeString);
		Template template = new DefinitionTemplate(file);
		return template;
	}

	private TemplateSet loadTemplateSet(File dir) {
		TemplateSet templateSet = new TemplateSet();
		templateSet.setName(dir.getName());
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				templateSet.addAllTemplates(loadTemplateSet(file));
			} else {
				templateSet.addTemplate(loadTemplate(file));
			}
		}
		return templateSet;
	}
}
