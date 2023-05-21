package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.generation.template.TemplateSet;

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

	private void process() {
		var curriculum = plan.getTargets();
		var goals = plan.getGoals();
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
