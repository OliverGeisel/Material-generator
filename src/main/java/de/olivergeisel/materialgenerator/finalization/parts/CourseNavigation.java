package de.olivergeisel.materialgenerator.finalization.parts;

public class CourseNavigation {
	private boolean hasPrevious;
	private boolean hasNext;
	private int count;
	private String nextChapter;
	private String previousChapter;
	private String overview;
	private String nextTask;
	private String previousTask;
	private String nextGroup;
	private String previousGroup;
	private MaterialLevel level;

	public CourseNavigation(MaterialLevel level) {
		this(level, 0, 0);
	}

	public CourseNavigation(MaterialLevel level, int number, int taskSize) {
		hasPrevious = number > 0;
		hasNext = number < taskSize - 1;
		count = number;
		this.level = level;
	}

	public CourseNavigation(CourseNavigation courseNavigation) {
		this.count = courseNavigation.count;
		this.hasNext = courseNavigation.hasNext;
		this.hasPrevious = courseNavigation.hasPrevious;
		this.level = new MaterialLevel(courseNavigation.level.chapter, courseNavigation.level.group, courseNavigation.level.task, courseNavigation.level.material);
		this.nextChapter = courseNavigation.nextChapter;
		this.nextGroup = courseNavigation.nextGroup;
		this.nextTask = courseNavigation.nextTask;
		this.overview = courseNavigation.overview;
		this.previousChapter = courseNavigation.previousChapter;
		this.previousGroup = courseNavigation.previousGroup;
		this.previousTask = courseNavigation.previousTask;
	}

	public CourseNavigation nextChapter(ChapterOrder nextChapter) {
		var back = new CourseNavigation(this);
		if (nextChapter != null) {
			back.nextChapter = nextChapter.getName();
		}
		back.count++;
		return back;
	}

	public CourseNavigation nextGroup(GroupOrder nextGroup) {
		var back = new CourseNavigation(this);
		if (nextGroup != null) {
			back.nextGroup = nextGroup.getName();
		}
		back.count++;
		return back;
	}

	public CourseNavigation nextTask(TaskOrder nextTask) {
		var back = new CourseNavigation(this);
		if (nextTask != null) {
			back.nextTask = nextTask.getName();
		}
		back.count++;
		return back;
	}

	//region setter/getter
	public boolean isHasPrevious() {
		return hasPrevious;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public int getCount() {
		return count;
	}

	public String getNextChapter() {
		return nextChapter;
	}

	public String getPreviousChapter() {
		return previousChapter;
	}

	public String getOverview() {
		return overview;
	}

	public String getNextTask() {
		return nextTask;
	}

	public String getPreviousTask() {
		return previousTask;
	}

	public String getNextGroup() {
		return nextGroup;
	}

	public String getPreviousGroup() {
		return previousGroup;
	}

	public MaterialLevel getLevel() {
		return level;
	}
//endregion

	public static class MaterialLevel {
		private String chapter;
		private String group;
		private String task;
		private String material;
		private Level level;

		public MaterialLevel() {
			this(null, null, null, null);
		}

		public MaterialLevel(String chapter) {
			this(chapter, null, null, null);
		}

		public MaterialLevel(String chapter, String group) {
			this(chapter, group, null, null);
		}

		public MaterialLevel(String chapter, String group, String task) {
			this(chapter, group, task, null);
		}

		public MaterialLevel(String chapter, String group, String task, String material) {
			this.chapter = chapter;
			this.group = group;
			this.task = task;
			if (chapter != null && !chapter.isEmpty()) {
				level = Level.CHAPTER;
			}
			if (group != null && !group.isEmpty()) {
				level = Level.GROUP;
			}
			if (task != null && !task.isEmpty()) {
				level = Level.TASK;
			}
			if (material != null && !material.isEmpty()) {
				level = Level.TASK;
			}
		}

		//region setter/getter
		public Level getLevel() {
			return level;
		}

		public String getChapter() {
			return chapter;
		}

		public String getGroup() {
			return group;
		}

		public String getTask() {
			return task;
		}
//endregion

		private enum Level {
			CHAPTER, GROUP, TASK
		}
	}
}
