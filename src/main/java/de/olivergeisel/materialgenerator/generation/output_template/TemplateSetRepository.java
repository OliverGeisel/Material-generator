package de.olivergeisel.materialgenerator.generation.output_template;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.UUID;

public interface TemplateSetRepository extends CrudRepository<TemplateSet, UUID> {
	@Override
	Streamable<TemplateSet> findAll();

}
