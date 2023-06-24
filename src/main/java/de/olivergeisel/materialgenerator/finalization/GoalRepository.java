package de.olivergeisel.materialgenerator.finalization;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends CrudRepository<Goal, UUID> {
	Optional<Goal> findByName(String name);
}
