package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrder;
import de.olivergeisel.materialgenerator.finalization.parts.TaskOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MaterialOrderRepository extends CrudRepository<MaterialOrder, UUID> {
}

interface ChapterOrderRepository extends CrudRepository<ChapterOrder, UUID> {
}

interface GroupOrderRepository extends CrudRepository<GroupOrder, UUID> {
}

interface TaskOrderRepository extends CrudRepository<TaskOrder, UUID> {
}

