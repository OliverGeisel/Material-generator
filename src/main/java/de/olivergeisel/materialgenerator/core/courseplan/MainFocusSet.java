package de.olivergeisel.materialgenerator.core.courseplan;

import java.util.*;

public class MainFocusSet {

	private List<MainFocus> focuses;


	public List<MainFocus> getFocuses() {
		return Collections.unmodifiableList(focuses);
	}

//
	public Map<MainFocus, CurriculumGoal> getFocusGoalMapping() {
		Map<MainFocus, CurriculumGoal> back = new HashMap<>();
		for (MainFocus focus : focuses) {
			back.put(focus, focus.getGoals());
		}
		return back;
	}

	public Set<CurriculumGoal> getGoals() {
		Set<CurriculumGoal> back = new HashSet<>();
		for (MainFocus focus : focuses) {
			back.add(focus.getGoals());
		}
		return Collections.unmodifiableSet(back);
	}
//
}
