package de.olivergeisel.materialgenerator.generation;

import de.olivergeisel.materialgenerator.generation.output_template.TemplateSet;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateSetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TemplateSetInitializer implements CommandLineRunner {

	private final String TEMPLATE_PATH = "templateSets";
	private final TemplateSetRepository repository;

	public TemplateSetInitializer(TemplateSetRepository repository) {
		this.repository = repository;
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
				repository.save(tempSet);
			}
		}
	}
}
