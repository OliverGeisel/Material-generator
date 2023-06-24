package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateSet;

import java.util.*;

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

	private void changed() {
		setUnchanged(false);
	}

	/**
	 * Create all Materials for a Goal
	 *
	 * @param expression
	 * @param knowledge
	 * @param templates
	 */
	private void create(ContentGoalExpression expression, KnowledgeNode knowledge, TemplateSet templates) {
		output = new GeneratorOutput();
		var templateName = templates.getName();
		switch (expression) {
			case FIRST_LOOK -> {
				materialForFirstLook(expression, knowledge, templateName);
				output.addMaterial();
			}
			case KNOW -> {
				materialForKnow(expression, knowledge, templateName);
				output.addMaterial();
			}
			case USE -> {
				materialForUse(expression, knowledge, templateName);
				output.addMaterial();
			}
			case CREATE -> {
				materailForCreate(expression, knowledge, templateName);
				output.addMaterial();
			}
			case TRANSLATE -> {
				materialForTranslate(expression, knowledge, templateName);
				output.addMaterial();
			}
			case COMMENT -> {
				materialForComment(expression, knowledge, templateName);
				output.addMaterial();
			}
			case CONTROL -> {
				materialForControl(expression, knowledge, templateName);
				output.addMaterial();
			}
		}
	}

	private List<MaterialAndMapping> materialForControl(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		return materialForComment(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForComment(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		return materialForTranslate(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForTranslate(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		return materailForCreate(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materailForCreate(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		return materialForUse(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForUse(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		var materials = materialForKnow(expression, knowledge, templateName);
		return materials;
	}

	private List<MaterialAndMapping> materialForKnow(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		var materials = materialForFirstLook(expression, knowledge, templateName);
		return materials;
	}

	private List<MaterialAndMapping> createAcronyms(KnowledgeNode knowledgeNode) {
		var mainTerm = knowledgeNode.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var relations = Arrays.stream(knowledgeNode.getRelations()).filter(it -> it.getType().equals(RelationType.IS_ACRONYM_FOR));
		relations.forEach(it -> {
			var targetId = it.getFromId();
			try {
				var element = Arrays.stream(knowledgeNode.getRelatedElements()).filter(elem -> elem.getId().equals(targetId)).findFirst().orElseThrow();

				Material acroMaterial = new Material(MaterialType.WIKI, mainTerm.getContent(), mainTerm.getId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(acroMaterial);
				mapping.add(mainTerm, element);
				back.add(new MaterialAndMapping(acroMaterial, mapping));
			} catch (Exception ignored) {
			}
		});
		return back;
	}

	private List<MaterialAndMapping> createDefinitions(KnowledgeNode knowledge) {
		var mainTerm = knowledge.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var definitionRelation = Arrays.stream(knowledge.getRelations()).filter(it -> it.getType().equals(RelationType.DEFINES));
		definitionRelation.forEach(it -> {
			var defId = it.getFromId();
			try {
				var definitionElement = Arrays.stream(knowledge.getRelatedElements()).filter(elem -> elem.getId().equals(defId)).findFirst().orElseThrow();

				Material defMaterial = new Material(MaterialType.WIKI, mainTerm);
				defMaterial.setValues(Map.of("term", mainTerm.getContent(), "definition", definitionElement.getContent()));
				MaterialMappingEntry mapping = new MaterialMappingEntry(defMaterial);
				mapping.add(mainTerm, definitionElement);
				back.add(new MaterialAndMapping(defMaterial, mapping));
			} catch (NoSuchElementException ignored) {

			}
		});
		return back;
	}

	private List<MaterialAndMapping> createExamples(KnowledgeNode knowledgeNode) {
		var mainTerm = knowledgeNode.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var relations = Arrays.stream(knowledgeNode.getRelations()).filter(it -> it.getType().equals(RelationType.EXAMPLE_FOR));
		relations.forEach(it -> {
			var targetId = it.getFromId();
			try {
				var element = Arrays.stream(knowledgeNode.getRelatedElements()).filter(elem -> elem.getId().equals(targetId)).findFirst().orElseThrow();


				Material material = new Material(MaterialType.EXAMPLE, mainTerm.getContent(), mainTerm.getId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(material);
				mapping.add(mainTerm, element);
				back.add(new MaterialAndMapping(material, mapping));
			} catch (Exception ignored) {
			}
		});
		return back;
	}

	private List<MaterialAndMapping> createLists() {
		return new ArrayList<>();
	}

	private List<MaterialAndMapping> createProofs(KnowledgeNode knowledgeNode) {
		var mainTerm = knowledgeNode.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var relations = Arrays.stream(knowledgeNode.getRelations()).filter(it -> it.getType().equals(RelationType.PROVEN_BY));
		relations.forEach(it -> {
			var targetId = it.getToId();
			try {
				var element = Arrays.stream(knowledgeNode.getRelatedElements()).filter(
						elem -> elem.getId().equals(targetId)).findFirst().orElseThrow();

				Material material = new Material(MaterialType.WIKI, mainTerm.getContent(), mainTerm.getId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(material);
				mapping.add(mainTerm, element);
				back.add(new MaterialAndMapping(material, mapping));
			} catch (Exception ignored) {
			}
		});
		return back;
	}

	private MaterialAndMapping createSynonyms(KnowledgeNode knowledgeNode) {
		var mainTerm = knowledgeNode.getMainElement();
		List<String> synonyms = new ArrayList<>();
		var relations = Arrays.stream(knowledgeNode.getRelations()).filter(it -> it.getType().equals(RelationType.IS_SYNONYM_FOR));
		Material material = new Material(MaterialType.WIKI, mainTerm);
		MaterialMappingEntry mapping = new MaterialMappingEntry(material);
		mapping.add(mainTerm);
		relations.forEach(it -> {
			if (it.getToId().equals(mainTerm.getId())) {
				var synonym = it.getFromId();
				var sysnonymElement = Arrays.stream(knowledgeNode.getRelatedElements()).filter(elem -> elem.getId().equals(synonym)).findFirst().orElseThrow();
				synonyms.add(sysnonymElement.getContent());
				mapping.add(sysnonymElement);
			}
		});
		material.setValues(Map.of("term", mainTerm.getContent()));
		return new MaterialAndMapping(material, mapping);
	}

	private List<MaterialAndMapping> createTexts(KnowledgeNode knowledgeNode) {
		var mainTerm = knowledgeNode.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		return back;
	}

	private List<MaterialAndMapping> materialForFirstLook(ContentGoalExpression expression, KnowledgeNode knowledge, String templateName) {
		var materials = createDefinitions(knowledge);
		materials.addAll(createAcronyms(knowledge));
		materials.addAll(createExamples(knowledge));
		return materials;
	}

	@Override
	public void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templates = templates;
		this.model = model;
		this.plan = plan;
	}

	@Override
	public void input(GeneratorInput input) {
		input(input.getTemplates(), input.getModel(), input.getPlan());
	}

	@Override
	public boolean update() {
		if (!isReady() || isUnchanged()) {
			return false;
		}
		process();
		setUnchanged(true);
		return true;
	}

	@Override
	public GeneratorOutput output() {
		return this.output;
	}

	public boolean isReady() {
		return templates != null && model != null && plan != null;
	}

	/**
	 * Loads the knowledge for the given expression
	 *
	 * @param elementId target expression
	 */
	private KnowledgeNode loadKnowledgeForElement(String elementId) {
		return model.getKnowledgeNode(elementId);
	}

	/**
	 * Processes the plan and creates the material
	 */
	private void process() {
		var goals = plan.getGoals();
		processGoals(goals.stream().toList());
		setUnchanged(true);
	}

	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			var expression = goal.getExpression();
			var target = goal.getMasterKeyword();
			var knowledge = loadKnowledgeForElement(target);
			create(expression, knowledge, templates);
		}
	}

	//region getter / setter
	private boolean isUnchanged() {
		return unchanged;
	}

	private void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}
//endregion
}
