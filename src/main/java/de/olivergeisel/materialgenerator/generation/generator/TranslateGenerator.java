package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.bloom_taxo_translate.BloomTaxonomicTranslator;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.template.TemplateSet;

import java.util.List;

public class TranslateGenerator implements Generator {

	private TemplateSet templates;
	private KnowledgeModel model;
	private CoursePlan plan;

	private boolean unchanged = false;
	private GeneratorOutput output;

	public TranslateGenerator(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templates = templates;
		this.model = model;
		this.plan = plan;
	}

	public TranslateGenerator() {

	}

	public TranslateGenerator(GeneratorInput input) {
		this.templates = input.getTemplates();
		this.model = input.getModel();
		this.plan = input.getPlan();
	}

	@Override
	public void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templates = templates;
		this.model = model;
		this.plan = plan;
	}

	@Override
	public void input(GeneratorInput input) {

	}

	@Override
	public boolean update() {
		if (!isReady() || isUnchanged()) {
			return false;
		}
		process();
		return true;
	}

	private void changed() {
		setUnchanged(false);
	}

	@Override
	public GeneratorOutput output() {
		return null;
	}

	private void loadForExpression(ContentGoalExpression expression) {
		switch (expression) {
			case FIRST_LOOK:
				break;
			case TRANSLATE:
				break;
			case KNOW:
				break;
			case USE:
				break;
			case COMMENT:
				break;
			case CONTROL:
				break;
			case CREATE:
				break;
		}
	}

	private void process() {
		var targets = plan.getTargets();
		var goals = plan.getGoals();

	}

	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			var term = goal.getMasterKeyword();
			var expression = goal.getExpression();
			var completeSentence = goal.getCompleteSentence();
			var targets = goal.getContent();
			var material = new Material();
			var goalExpression = BloomTaxonomicTranslator.translate(expression.name());
		}
	}

	//
	public boolean isReady() {
		return templates != null && model != null && plan != null;
	}

	private boolean isUnchanged() {
		return unchanged;
	}

	private void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}
//
}
