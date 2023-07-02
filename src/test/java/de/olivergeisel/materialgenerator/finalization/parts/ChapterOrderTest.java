package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.core.courseplan.structure.Relevance;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.finalization.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChapterOrderTest {


	private ChapterOrder chapterOrder;
	@Mock
	private Goal goal;
	@Mock
	private GroupOrder groupOrder;
	@Mock
	private StructureChapter structureChapter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(goal.getName()).thenReturn("TestGoal");
		when(groupOrder.getTaskOrder()).thenReturn(List.of(mock(TaskOrder.class)));
		when(groupOrder.getRelevance()).thenReturn(Relevance.IMPORTANT);

		chapterOrder = new ChapterOrder(structureChapter, Set.of(goal));
	}

	@Test
	void createTest() {
		assertNotNull(chapterOrder, "ChapterOrder must be created");
	}

	@Test
	void createNullChapterTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ChapterOrder(null, Set.of(goal));
		}, "chapter must not be null");
	}
}