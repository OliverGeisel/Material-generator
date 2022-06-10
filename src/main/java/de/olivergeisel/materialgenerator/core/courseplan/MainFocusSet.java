package de.olivergeisel.materialgenerator.core.courseplan;

import java.util.*;

public class MainFocusSet {

	private List<MainFocus> focuses;


	public List<MainFocus> getFocuses() {
		return Collections.unmodifiableList(focuses);
	}

	public Set<CurriculumGoal> getGoals() {
		Set<CurriculumGoal> back = new HashSet<>();
		for (MainFocus focus : focuses) {
			back.add(focus.getGoal());
		}
		return Collections.unmodifiableSet(back);
	}

	public Map<MainFocus, CurriculumGoal> getFocusGoalMapping() {
		Map<MainFocus, CurriculumGoal> back = new HashMap<>();
		for (MainFocus focus : focuses) {
			back.put(focus, focus.getGoal());
		}
		return back;
	}
}
