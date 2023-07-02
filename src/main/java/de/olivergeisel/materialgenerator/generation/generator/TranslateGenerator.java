package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.*;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.BasicTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.DefinitionTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.ListTemplate;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.TemplateInfo;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Generator for @see Material objects for MDtea. This Generator can only create the simplest form of Material in MDTea.
 */
public class TranslateGenerator implements Generator {

	private final Set<TemplateInfo> basicTemplateInfo = new HashSet<>();
	private final Logger logger = org.slf4j.LoggerFactory.getLogger(TranslateGenerator.class);
	private TemplateSet templateSet;
	private KnowledgeModel model;
	private CoursePlan plan;
	private boolean unchanged = false;
	private GeneratorOutput output;

	public TranslateGenerator() {

	}

	public TranslateGenerator(GeneratorInput input) {
		this.templateSet = input.getTemplates();
		this.model = input.getModel();
		this.plan = input.getPlan();
	}

	public TranslateGenerator(TemplateSet templateSet, KnowledgeModel model, CoursePlan plan) {
		this.templateSet = templateSet;
		this.model = model;
		this.plan = plan;
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType.
	 * It searches in the relatedElements of the KnowledgeNode.
	 *
	 * @param mainKnowledge KnowledgeNode to search in
	 * @param type          RelationType to search for. Should be a Relation, that points to the main Element of the KnowledgeNode
	 * @return Set of Relations that match the RelationType
	 */
	private static Set<Relation> getWantedRelationsFromRelated(KnowledgeNode mainKnowledge, RelationType type) {
		return Arrays.stream(mainKnowledge.getRelatedElements()).flatMap(it -> it.getRelations().stream().filter(relation -> relation.getType().equals(type))).collect(Collectors.toSet());
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType. Search only in the mainElement of the KnowledgeNode.
	 *
	 * @param knowledgeNode KnowledgeNode to search in
	 * @param type          RelationType to search for. Should be a Relation, that goes from the main Element of the KnowledgeNode. Like DefinedBy
	 * @return Set of Relations that match the RelationType
	 */
	private static Set<Relation> getWantedRelationsFromMain(KnowledgeNode knowledgeNode, RelationType type) {
		return knowledgeNode.getMainElement().getRelations().stream().filter(relation -> relation.getType().equals(type)).collect(Collectors.toSet());
	}

	private static String getUniqueMaterialName(List<MaterialAndMapping> back, String startName, String defId) {
		String name = startName;
		final String finalName = name;
		if (back.stream().anyMatch(mat -> mat.material().getName().equals(finalName))) {
			name = defId;
		}
		return name;
	}

	private void changed() {
		setUnchanged(false);
	}

	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			var expression = goal.getExpression();
			var masterKeyword = goal.getMasterKeyword();
			try {
				var knowledge = loadKnowledgeForStructure(masterKeyword);
				knowledge.forEach(it -> it.setGoal(goal));
				createMaterialForGoal(expression, knowledge, templateSet);
			} catch (NoSuchElementException e) {
				logger.info("No knowledge found for MasterKeyword {}", masterKeyword);
			}
		}
	}

	/**
	 * Create all Materials for a Goal
	 *
	 * @param expression the Goal to create Materials for
	 * @param knowledge  the Knowledge to use
	 * @param templates  the Templates to use
	 */
	private void createMaterialForGoal(ContentGoalExpression expression, Set<KnowledgeNode> knowledge, TemplateSet templates) {
		var templateName = templates.getName();
		var materialAndMapping = switch (expression) {
			case FIRST_LOOK -> materialForFirstLook(knowledge, templateName);
			case KNOW -> materialForKnow(knowledge, templateName);
			case USE -> materialForUse(knowledge, templateName);
			case CREATE -> materialForCreate(knowledge, templateName);
			case TRANSLATE -> materialForTranslate(knowledge, templateName);
			case COMMENT -> materialForComment(knowledge, templateName);
			case CONTROL -> materialForControl(knowledge, templateName);
		};
		var material = materialAndMapping.stream().map(MaterialAndMapping::material).toList();
		var mapping = materialAndMapping.stream().map(MaterialAndMapping::mapping).toList();
		output.addMaterial(material);
		output.addMapping(mapping);
	}

	private List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge, String templateName) {
		return materialForComment(knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge, String templateName) {
		return materialForTranslate(knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge, String templateName) {
		return materialForCreate(knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge, String templateName) {
		return materialForUse(knowledge, templateName);
	}

	private List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge, String templateName) {
		var materials = materialForKnow(knowledge, templateName);
		return materials;
	}

	private List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge, String templateName) {
		var materials = materialForFirstLook(knowledge, templateName);
		materials.addAll(createProofs(knowledge));
		materials.addAll(createExamples(knowledge));
		return materials;
	}

	private List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge, String templateName) {
		var materials = createDefinitions(knowledge);
		//materials.addAll(createAcronyms(knowledge));
		//materials.addAll(createExamples(knowledge));
		return materials;
	}

	/**
	 * Create Definition Materials for a KnowledgeNode
	 *
	 * @param knowledge KnowledgeNodes to create Materials for
	 * @return List of Materials
	 * @throws NoTemplateInfoException  if no Definition Template is found
	 * @throws IllegalArgumentException if no Definition Relation is found or if the KnowledgeNode has no MasterKeyword
	 */
	private List<MaterialAndMapping> createDefinitions(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException, IllegalArgumentException {
		var templateInfo = getBasicTemplateInfo(DefinitionTemplate.class);
		// var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElseThrow();
		var mainKnowledge = knowledge.stream().filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM))
				//&& it.getMainElement().getContent().contains(masterKeyword))
				.findFirst().orElseThrow(); // todo need a Class that say a MasterKeyword is matching to the mainElement.
		List<MaterialAndMapping> back = new ArrayList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var definitionRelations = getWantedRelationsFromRelated(mainKnowledge, RelationType.DEFINES);
		definitionRelations.forEach(it -> {
			var defId = it.getFromId();
			try {
				var definitionElement = Arrays.stream(mainKnowledge.getRelatedElements())
						.filter(elem -> elem.getId().equals(defId)).findFirst().orElseThrow();
				String name = getUniqueMaterialName(back, "Definition " + mainTerm.getContent(), defId);
				var values = Map.of("term", mainTerm.getContent(), "definition", definitionElement.getContent());
				var materialAndMapping = new MaterialCreator().createWikiMaterial(mainTerm, name, templateInfo, values,
						definitionElement);
				back.add(materialAndMapping);
			} catch (NoSuchElementException ignored) {
				logger.warn("No definition found for {}", mainTerm.getContent());
			}
		});
		return back;
	}

	private List<MaterialAndMapping> createLists(List<KnowledgeNode> knowledge) {
		var templateInfo = basicTemplateInfo.stream().filter(ListTemplate.class::isInstance).findFirst().orElseThrow(() -> new NoTemplateInfoException("No List Template found"));
		var mainKnowledge = knowledge.stream().filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM)).findFirst().orElseThrow();
		var mainTerm = mainKnowledge.getMainElement();
		var back = new ArrayList<MaterialAndMapping>();
		var material = new Material(MaterialType.WIKI, mainTerm);
		material.setTemplate(templateInfo);
		material.setName("List " + mainTerm.getContent());
		material.setValues(Map.of("term", mainTerm.getContent()));

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

	private List<MaterialAndMapping> createTexts(List<KnowledgeNode> knowledgeNode) {
		var templateInfo = basicTemplateInfo.stream().filter(TextTemplate.class::isInstance).findFirst().orElseThrow(() -> new NoTemplateInfoException("No Definition Template found"));
		var mainKnowledge = knowledgeNode.stream().filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM)).findFirst().orElseThrow();
		var mainTerm = mainKnowledge.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var textRelations = Arrays.stream(mainKnowledge.getRelations()).filter(it -> it.getType().equals(RelationType.HAS));
		textRelations.forEach(it -> {
			var textId = it.getFromId();
			try {
				var textElement = Arrays.stream(mainKnowledge.getRelatedElements()).filter(elem -> elem.getId().equals(textId)).findFirst().orElseThrow();
				Material textMaterial = new Material(MaterialType.WIKI, mainTerm);
				textMaterial.setName(mainTerm.getContent());
				textMaterial.setTemplate(templateInfo);
				textMaterial.setValues(Map.of("term", mainTerm.getContent(), "content", textElement.getContent()));
				MaterialMappingEntry mapping = new MaterialMappingEntry(textMaterial);
				mapping.add(mainTerm, textElement);
				back.add(new MaterialAndMapping(textMaterial, mapping));
			} catch (NoSuchElementException ignored) {
				logger.warn("No text found for {}", mainTerm.getContent());
			}
		});
		return back;
	}

	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			var expression = goal.getExpression();
			var masterKeyword = goal.getMasterKeyword();
			try {
				var knowledge = loadKnowledgeForStructure(masterKeyword);
				create(expression, knowledge, templateSet);
			} catch (NoSuchElementException e) {
				logger.info("No knowledge found for element {}", masterKeyword);
			}
		}
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

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		return model.getKnowledgeNodesFor(structureId);
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

	private List<MaterialAndMapping> createProofs(KnowledgeNode knowledgeNode) {
		var mainTerm = knowledgeNode.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var relations = Arrays.stream(knowledgeNode.getRelations()).filter(it -> it.getType().equals(RelationType.PROVEN_BY));
		relations.forEach(it -> {
			var targetId = it.getToId();
			try {
				var element = Arrays.stream(knowledgeNode.getRelatedElements()).filter(elem -> elem.getId().equals(targetId)).findFirst().orElseThrow();

				Material material = new Material(MaterialType.WIKI, mainTerm.getContent(), mainTerm.getId(), mainTerm.getStructureId());
				MaterialMappingEntry mapping = new MaterialMappingEntry(material);
				mapping.add(mainTerm, element);
				back.add(new MaterialAndMapping(material, mapping));
			} catch (Exception ignored) {
				logger.debug("No proof found for {}", mainTerm.getContent());
			}
		});
		return back;
	}

	/**
	 * Returns the basic templateInfo for the given class.
	 *
	 * @param templateInfoClass the class of the templateInfo
	 * @return the templateInfo
	 * @throws NoTemplateInfoException if no templateInfo is found
	 */
	private TemplateInfo getBasicTemplateInfo(Class<? extends TemplateInfo> templateInfoClass) throws NoTemplateInfoException {
		return basicTemplateInfo.stream().filter(templateInfoClass::isInstance).findFirst().orElseThrow(() -> new NoTemplateInfoException(String.format("No Template %s found", templateInfoClass.getSimpleName())));
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		return model.getKnowledgeNodesFor(structureId, true);
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
		this.templateSet = templates;
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

	//region setter/getter
	public boolean isReady() {
		return templateSet != null && model != null && plan != null;
	}

	private boolean isUnchanged() {
		return unchanged;
	}

	private void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}

	public void setTemplateSet(TemplateSet templateSet) {
		this.templateSet = templateSet;
		changed();
	}

	public void setModel(KnowledgeModel model) {
		this.model = model;
		changed();
	}

	public void setPlan(CoursePlan plan) {
		this.plan = plan;
		changed();
	}

	public void setBasicTemplateInfo(Set<BasicTemplate> basicTemplateInfo) {
		this.basicTemplateInfo.clear();
		this.basicTemplateInfo.addAll(basicTemplateInfo);
		changed();
	}
//endregion
}
