package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.generation.output_template.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class TemplateSetInitializer implements CommandLineRunner {

	private final String TEMPLATE_PATH = "templateSets";
	private final TemplateSetRepository repository;
	private final TemplateInfoRepository templateInfoRepository;

	public TemplateSetInitializer(TemplateSetRepository repository, TemplateInfoRepository templateInfoRepository) {
		this.repository = repository;
		this.templateInfoRepository = templateInfoRepository;
	}

	private Set<ExtraTemplate> getExtraTemplates(File templatePath) {
		var back = new HashSet<>();
		var extraTemplates = Arrays.stream(templatePath.listFiles())
				.filter(it -> !BasicTemplates.TEMPLATES.contains(it.getName().replace(".html", "").toUpperCase())).toList();
		extraTemplates.forEach(it -> {
			var name = it.getName().replace(".html", "").toUpperCase();
			back.add(new ExtraTemplate(new TemplateType(name), it.getName()));
		});
		return null;// Collections.unmodifiableSet(extraTemplates);
	}

	private void saveBasicTemplates() {
		// todo later when to save basic templates
		var basicTemplates = new BasicTemplates();
		//templateInfoRepository.saveAll(basicTemplates.getTemplates());
	}

	@Override
	public void run(String... args) throws Exception {
		File templatePath;
		var baseURI = TemplateSetInitializer.class.getClassLoader().getResource(TEMPLATE_PATH);
		if (baseURI == null) {
			throw new Exception("Template path not found");
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
