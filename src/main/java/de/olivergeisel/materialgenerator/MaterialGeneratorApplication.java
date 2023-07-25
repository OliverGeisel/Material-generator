package de.olivergeisel.materialgenerator;

import de.olivergeisel.materialgenerator.finalization.ImageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class MaterialGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaterialGeneratorApplication.class, args);
	}

	@Bean
	CommandLineRunner initFiles(FileSystemStorageService storageService) {
		return args -> storageService.init();
	}

	@Bean
	CommandLineRunner initImages(ImageService storageService) {
		return args -> storageService.init();
	}
}
