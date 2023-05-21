package de.olivergeisel.materialgenerator.core.courseplan.bloom_taxo_translate;

import de.olivergeisel.materialgenerator.core.courseplan.CurriculumGoalExpression;

public class BloomTaxonomicTranslator {

	public static CurriculumGoalExpression translate(String word) {
		CurriculumGoalExpression level = CurriculumGoalExpression.valueOf(word.toUpperCase());
		return level;
	}
}
