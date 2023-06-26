package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.generation.output_template.template_content.TemplateInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TemplateInfoRepository extends CrudRepository<TemplateInfo, UUID> {
}
