package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.finalization.parts.*;
import de.olivergeisel.materialgenerator.generation.generator.Material;
import de.olivergeisel.materialgenerator.generation.generator.MaterialAndMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FinalizationService {

	private final DownloadManager downloadManager;

	private final MaterialOrderRepository materialOrderRepository;
	private final ChapterOrderRepository chapterOrderRepository;
	private final GroupOrderRepository groupOrderRepository;
	private final TaskOrderRepository taskOrderRepository;
	private final RawCourseRepository rawCourseRepository;
	private final CourseMetadataFinalizationRepository metadataRepository;
	private final GoalRepository goalRepository;
	private final TopicRepository topicRepository;

	public FinalizationService(DownloadManager downloadManager, MaterialOrderRepository materialOrderRepository,
							   ChapterOrderRepository chapterOrderRepository,
							   GroupOrderRepository groupOrderRepository, TaskOrderRepository taskOrderRepository,
							   RawCourseRepository rawCourseRepository, CourseMetadataFinalizationRepository metadataRepository, GoalRepository goalRepository,
							   TopicRepository topicRepository) {
		this.downloadManager = downloadManager;
		this.materialOrderRepository = materialOrderRepository;
		this.chapterOrderRepository = chapterOrderRepository;
		this.groupOrderRepository = groupOrderRepository;
		this.taskOrderRepository = taskOrderRepository;
		this.rawCourseRepository = rawCourseRepository;
		this.metadataRepository = metadataRepository;
		this.goalRepository = goalRepository;
		this.topicRepository = topicRepository;
	}

	public RawCourse createRawCourse(CoursePlan coursePlan, String template, Set<MaterialAndMapping> materials) {
		var cGoals = coursePlan.getGoals();
		var goals = createGoals(cGoals);
		var rawCourse = new RawCourse(coursePlan, template, goals);
		rawCourse.assignMaterial(materials);
		saveMetadata(rawCourse.getMetadata());
		saveMaterialOrder(rawCourse.getMaterialOrder());
		return rawCourseRepository.save(rawCourse);
	}

	private Set<Goal> createGoals(Set<ContentGoal> goals) {
		var back = new HashSet<Goal>();
		goals.forEach(contentGoal -> goalRepository.findByName(contentGoal.getName()).ifPresentOrElse(
				back::add,
				() -> {
					var goal = new Goal(contentGoal);
					goal = goalRepository.save(goal);
					Goal finalGoal = goal;
					goal.getTopics().forEach(topic -> topic.updateGoal(finalGoal));
					topicRepository.saveAll(goal.getTopics());
					back.add(goal);
				}));
		return back;
	}

	private void saveMaterialOrder(MaterialOrder materialOrder) {
		saveChapterOrder(materialOrder.getChapterOrder());
		materialOrderRepository.save(materialOrder);
	}

	private void saveMetadata(CourseMetadataFinalization metadata) {
		metadataRepository.save(metadata);
	}

	private void saveChapterOrder(List<ChapterOrder> chapterOrder) {
		chapterOrder.forEach(chapter -> {
			saveGroupOrder(chapter.getGroupOrder());
			chapterOrderRepository.save(chapter);
		});
	}

	private void saveGroupOrder(List<GroupOrder> groupOrder) {
		groupOrder.forEach(group -> {
			saveTaskOrder(group.getTaskOrder());
			groupOrderRepository.save(group);
		});
	}

	private void saveTaskOrder(List<TaskOrder> taskOrder) {
		taskOrderRepository.saveAll(taskOrder);
	}

	public void moveUp(UUID id, UUID parentChapterId, UUID parentGroupId, UUID parentTaskId, UUID idUp) {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getMaterialOrder();
		switch (order.find(idUp)) {
			case ChapterOrder chapter -> order.moveUp(chapter);
			case GroupOrder group -> {
				var chapter = order.findChapter(parentChapterId);
				chapter.moveUp(group);
			}
			case TaskOrder task -> {
				var group = order.findGroup(parentGroupId);
				group.moveUp(task);
			}
			case Material material -> {
				var task = order.findTask(parentTaskId);
				task.moveUp(material);
			}
			default -> throw new IllegalStateException("Unexpected value: " + order.find(idUp));
		}
		rawCourseRepository.save(course);
	}

	public void moveDown(UUID id, UUID parentChapterId, UUID parentGroupId, UUID parentTaskId, UUID idDown) {
		var course = rawCourseRepository.findById(id).orElseThrow();
		var order = course.getMaterialOrder();
		switch (order.find(idDown)) {
			case ChapterOrder chapter -> order.moveDown(chapter);
			case GroupOrder group -> {
				var chapter = order.findChapter(parentChapterId);
				chapter.moveDown(group);
			}
			case TaskOrder task -> {
				var group = order.findGroup(parentGroupId);
				group.moveDown(task);
			}
			case Material material -> {
				var task = order.findTask(parentTaskId);
				task.moveDown(material);
			}
			default -> throw new IllegalStateException("Unexpected value: " + order.find(idDown));
		}
		rawCourseRepository.save(course);
	}

	public void generateAndDownloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		downloadManager.createSingle("test", request, response);
	}

	public void exportCourse(UUID id, HttpServletRequest request, HttpServletResponse response) {
		generateAndDownloadTemplates(rawCourseRepository.findById(id).orElseThrow(), request, response);
	}

	public void generateAndDownloadTemplates(@PathVariable("courseId") RawCourse plan, HttpServletRequest request, HttpServletResponse response) {
		var zipName = plan.getMetadata().getName().orElse("course");
		var structure = plan.getMaterialOrder();

		downloadManager.createZip(zipName, plan.getTemplateName(), structure, request, response);
	}
}
