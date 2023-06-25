package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.generation.output_template.*;
import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class TemplateService {
	public static final String TEMPLATE_SET_FROM_TEMPLATES_FOLDER = "../templateSets/";
	private final Set<String> basicTemplates = Set.of("DEFINITION", "ACRONYM", "TEXT", "SYNONYM", "LIST");
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

	private TemplateInfo loadTemplate(File file) {
		var typeString = file.getName().replace(".html", "").toUpperCase();
		TemplateType type = TemplateType.valueOf(typeString);
		return new ExtraTemplate(file, type);
	}

	private TemplateSet loadTemplateSet(File dir) {
		TemplateSet templateSet = new TemplateSet();
		templateSet.setName(dir.getName());
		setBasicTemplates(templateSet, dir);
		var extraTemplates = Arrays.stream(dir.listFiles()).filter(file -> !basicTemplates.contains(file.getName())).toList();
		for (File file : extraTemplates) {
			if (file.isDirectory()) {
				templateSet.addAllTemplates(loadExtraTemplates(file));
			} else {
				templateSet.addTemplate(loadTemplate(file));
			}
		}
		return templateSet;
	}

	private void setBasicTemplates(TemplateSet templateSet, File dir) {
		var containingTemplates = Arrays.stream(dir.listFiles()).map(it -> it.getName().replace(".html", "").toUpperCase()).toList();
		if (containingTemplates.contains("DEFINITION")) {
			templateSet.setDefinitionTemplate(new DefinitionTemplate(new File(dir, "DEFINITION" + ".html")));
		}
		if (containingTemplates.contains("ACRONYM")) {
			templateSet.setAcronymTemplate(new AcronymTemplate(new File(dir, "ACRONYM" + ".html")));
		}
		if (containingTemplates.contains("TEXT")) {
			templateSet.setTextTemplate(new TextTemplate(new File(dir, "TEXT" + ".html")));
		}
		if (containingTemplates.contains("SYNONYM")) {
			templateSet.setSynonymTemplate(new SynonymTemplate(new File(dir, "SYNONYM" + ".html")));
		}
		if (containingTemplates.contains("LIST")) {
			templateSet.setListTemplate(new ListTemplate(new File(dir, "LIST" + ".html")));
		}
	}

	private Set<TemplateInfo> loadExtraTemplates(File dir) {
		Set<TemplateInfo> templateSet = new HashSet<>();
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				templateSet.addAll(loadExtraTemplates(file));
			} else {
				templateSet.add(loadTemplate(file));
			}
		}
		return templateSet;
	}
}
