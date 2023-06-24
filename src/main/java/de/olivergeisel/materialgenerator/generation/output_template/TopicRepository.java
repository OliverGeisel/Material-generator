package de.olivergeisel.materialgenerator.generation.output_template;

import de.olivergeisel.materialgenerator.finalization.Topic;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TopicRepository extends CrudRepository<Topic, UUID> {
}
