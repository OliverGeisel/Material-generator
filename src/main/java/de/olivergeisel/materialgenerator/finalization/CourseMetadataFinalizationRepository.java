package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.finalization.parts.CourseMetadataFinalization;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CourseMetadataFinalizationRepository extends CrudRepository<CourseMetadataFinalization, UUID> {
}
