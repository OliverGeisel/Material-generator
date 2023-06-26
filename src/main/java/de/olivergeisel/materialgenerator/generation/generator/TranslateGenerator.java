package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.output_template.TemplateSet;
import org.slf4j.Logger;

import java.util.*;

public class TranslateGenerator implements Generator {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(TranslateGenerator.class);

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
	private void create(ContentGoalExpression expression, List<KnowledgeNode> knowledge, TemplateSet templates) {
		var templateName = templates.getName();
		var materialAndMapping = switch (expression) {
			case FIRST_LOOK -> materialForFirstLook(expression, knowledge, templateName);
			case KNOW -> materialForKnow(expression, knowledge, templateName);
			case USE -> materialForUse(expression, knowledge, templateName);
			case CREATE -> materailForCreate(expression, knowledge, templateName);
			case TRANSLATE -> materialForTranslate(expression, knowledge, templateName);
			case COMMENT -> materialForComment(expression, knowledge, templateName);
			case CONTROL -> materialForControl(expression, knowledge, templateName);
		};
		var material = materialAndMapping.stream().map(MaterialAndMapping::material).toList();
		var mapping = materialAndMapping.stream().map(MaterialAndMapping::mapping).toList();
		output.addMaterial(material);
		output.addMapping(mapping);
	}

	private List<MaterialAndMapping> materialForControl(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
		return materialForComment(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForComment(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
		return materialForTranslate(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForTranslate(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
		return materailForCreate(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materailForCreate(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
		return materialForUse(expression, knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForUse(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
		var materials = materialForKnow(expression, knowledge, templateName);
		return materials;
	}

	private List<MaterialAndMapping> materialForKnow(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
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

				Material acroMaterial = new Material(MaterialType.WIKI, mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(acroMaterial);
				mapping.add(mainTerm, element);
				back.add(new MaterialAndMapping(acroMaterial, mapping));
			} catch (Exception ignored) {
			}
		});
		return back;
	}

	private List<MaterialAndMapping> createDefinitions(List<KnowledgeNode> knowledge) {
		var mainKnowledge = knowledge.stream().filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM)).findFirst().orElseThrow();
		var mainTerm = mainKnowledge.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var definitionRelation = Arrays.stream(mainKnowledge.getRelations()).filter(it -> it.getType().equals(RelationType.DEFINES));
		definitionRelation.forEach(it -> {
			var defId = it.getFromId();
			try {
				var definitionElement = Arrays.stream(mainKnowledge.getRelatedElements()).filter(elem -> elem.getId().equals(defId)).findFirst().orElseThrow();
				Material defMaterial = new Material(MaterialType.WIKI, mainTerm);
				defMaterial.setName("Definition " + mainTerm.getContent());
				defMaterial.setValues(Map.of("term", mainTerm.getContent(), "definition", definitionElement.getContent()));
				MaterialMappingEntry mapping = new MaterialMappingEntry(defMaterial);
				mapping.add(mainTerm, definitionElement);
				back.add(new MaterialAndMapping(defMaterial, mapping));
			} catch (NoSuchElementException ignored) {
				logger.warn("No definition found for {}", mainTerm.getContent());
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


				Material material = new Material(MaterialType.EXAMPLE, mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId());
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

				Material material = new Material(MaterialType.WIKI, mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId());
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

	private List<MaterialAndMapping> materialForFirstLook(ContentGoalExpression expression, List<KnowledgeNode> knowledge, String templateName) {
		var materials = createDefinitions(knowledge);
		//materials.addAll(createAcronyms(knowledge));
		//materials.addAll(createExamples(knowledge));
		return materials;
	}

	/**
	 * Initial method to set the input for the generator. All parameters cant be null.
	 *
	 * @param templates Templates that should be used for the generation.
	 * @param model     The current KnowledgeModel.
	 * @param plan      Plan with curriculum for which the generator should generate materials.
	 */
	@Override
	public void input(TemplateSet templates, KnowledgeModel model, CoursePlan plan) {
		this.templates = templates;
		this.model = model;
		this.plan = plan;
	}

	/**
	 * Initial method to set the input for the generator. This needs only the GeneratorInput object.
	 *
	 * @param input All input to be needed for the generator. Must be valid and not be null.
	 */
	@Override
	public void input(GeneratorInput input) {
		input(input.getTemplates(), input.getModel(), input.getPlan());
	}

	/**
	 * Method to start the generation process. This method should be called after the input method.
	 *
	 * @return True if the generation was successful, false if not.
	 */
	@Override
	public boolean update() {
		if (!isReady() || isUnchanged()) {
			return false;
		}
		process();
		setUnchanged(true);
		return true;
	}

	/**
	 * Method to get the output of the generation process. This method should be called after the update method.
	 *
	 * @return all Materials for the given input.
	 */
	@Override
	public GeneratorOutput output() {
		return this.output;
	}

	/**
	 * Loads the knowledge for the given expression
	 *
	 * @param elementId target expression
	 * @return KnowledgeNode for the given expression
	 * @throws NoSuchElementException if the expression is not found in the model
	 */
	private KnowledgeNode loadKnowledgeForElement(String elementId) throws NoSuchElementException {
		return model.getKnowledgeNode(elementId);
	}

	/**
	 * Processes the plan and creates the material
	 */
	private void process() {
		var goals = plan.getGoals();
		output = new GeneratorOutput();
		processGoals(goals.stream().toList());
		setUnchanged(true);
	}

	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			var expression = goal.getExpression();
			var target = goal.getMasterKeyword();
			try {
				var knowledge = loadKnowledgeForStructure(target);
				create(expression, knowledge, templates);
			} catch (NoSuchElementException e) {
				logger.info("No knowledge found for element {}", target);
			}
		}
	}

	private List<KnowledgeNode> loadKnowledgeForStructure(String structureId) {
		if (structureId == null) {
			return new ArrayList<>();
		}
		return model.getKnowledgeNodesFor(structureId);
	}

	//region setter/getter
	public boolean isReady() {
		return templates != null && model != null && plan != null;
	}

	private boolean isUnchanged() {
		return unchanged;
	}

	private void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}
//endregion
}
