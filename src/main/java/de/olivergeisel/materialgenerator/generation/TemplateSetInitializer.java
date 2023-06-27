package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.generation.output_template.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class TemplateSetInitializer implements CommandLineRunner {

	private final Set<String> ignoredFiles = Set.of("exclude", "include", "INCLUDE", "ignore", "help", "COURSE", "MATERIAL", "CHAPTER", "GROUP");
	private final String TEMPLATE_PATH = "templateSets";
	private final TemplateSetRepository repository;
	private final TemplateInfoRepository templateInfoRepository;

	public TemplateSetInitializer(TemplateSetRepository repository, TemplateInfoRepository templateInfoRepository) {
		this.repository = repository;
		this.templateInfoRepository = templateInfoRepository;
	}

	private Set<ExtraTemplate> getExtraTemplates(File templatePath) {
		var back = new HashSet<ExtraTemplate>();
		var extraTemplates = Arrays.stream(templatePath.listFiles()).filter(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			return !ignoredFiles.contains(name) && !BasicTemplates.TEMPLATES.contains(name);
		}).toList();
		extraTemplates.forEach(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			back.add(new ExtraTemplate(new TemplateType(name), it.getName()));
		});
		return back;// Collections.unmodifiableSet(extraTemplates);
	}

	private void saveBasicTemplates() {
		// todo later when to save basic templates
		var basicTemplates = new BasicTemplates();
		//templateInfoRepository.saveAll(basicTemplates.getTemplates());
	}

	@Override
	public void run(String... args) throws IllegalArgumentException, URISyntaxException {
		File templatePath;
		var baseURI = TemplateSetInitializer.class.getClassLoader().getResource(TEMPLATE_PATH);
		if (baseURI == null) {
			throw new IllegalArgumentException("Template path not found");
		}
		templatePath = new File(baseURI.toURI());
		for (File file : templatePath.listFiles()) {
			if (file.isDirectory()) {
				var tempSet = new TemplateSet(file.getName());
				tempSet.addAllTemplates(getExtraTemplates(file));
				templateInfoRepository.saveAll(tempSet.getExtraTemplates());
				repository.save(tempSet);
			}
		}
	}
}
