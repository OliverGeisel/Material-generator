package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoal;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentGoalExpression;
import de.olivergeisel.materialgenerator.core.courseplan.content.ContentTarget;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.Code;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeElement;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.element.KnowledgeType;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.Relation;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.relation.RelationType;
import de.olivergeisel.materialgenerator.generation.KnowledgeNode;
import de.olivergeisel.materialgenerator.generation.material.*;
import de.olivergeisel.materialgenerator.generation.templates.TemplateSet;
import de.olivergeisel.materialgenerator.generation.templates.template_infos.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Generator for {@link Material} objects for MDTea.
 * <p>
 * This Generator can only create the simplest form of Material in
 * MDTea. It can create Definitions, Lists, Textes Code and Examples.
 *
 * @author Oliver Geisel
 * @version 1.0.0
 * @see Generator
 * @see Material
 * @see TemplateSet
 * @see KnowledgeModel
 * @see CoursePlan
 * @see KnowledgeNode
 * @since 0.2.0
 */
public class TranslateGenerator implements Generator {

	private static final String            UNKNOWN           = "Unknown";
	private final        Set<TemplateInfo> basicTemplateInfo = new HashSet<>();
	private final        Logger            logger            = org.slf4j.LoggerFactory.getLogger(
			TranslateGenerator.class);
	private              TemplateSet       templateSet;
	private              KnowledgeModel    model;
	private              CoursePlan        plan;
	private              boolean           unchanged         = false;
	private              GeneratorOutput   output;

	public TranslateGenerator() {

	}

	public TranslateGenerator(GeneratorInput input) {
		this(input.getTemplates(), input.getModel(), input.getPlan());
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
	 * @param type          RelationType to search for. Should be a Relation, that points to the main Element of the
	 *                      KnowledgeNode
	 * @return Set of Relations that match the RelationType
	 */
	private static Set<Relation> getWantedRelationsFromRelated(KnowledgeNode mainKnowledge, RelationType type) {
		return Arrays.stream(mainKnowledge.getRelatedElements())
					 .flatMap(it -> it.getRelations().stream().filter(relation -> relation.getType().equals(type)))
					 .collect(Collectors.toSet());
	}

	/**
	 * Get all Relations of a KnowledgeNode that match a RelationType. Search only in the mainElement of the
	 * KnowledgeNode.
	 *
	 * @param knowledgeNode KnowledgeNode to search in
	 * @param type          RelationType to search for. Should be a Relation, that goes from the main Element of the
	 *                      KnowledgeNode. Like DefinedBy
	 * @return Set of Relations that match the RelationType
	 */
	private static Set<Relation> getWantedRelationsFromMain(KnowledgeNode knowledgeNode, RelationType type) {
		return knowledgeNode.getMainElement().getRelations().stream()
							.filter(relation -> relation.getType().equals(type)).collect(Collectors.toSet());
	}

	private static String getUniqueMaterialName(List<MaterialAndMapping> materials, String startName,
			String alternativeName) {
		String name = startName;
		final String finalName = name;
		if (materials.stream().anyMatch(mat -> mat.material().getName().equals(finalName))) {
			name = alternativeName;
		}
		return name;
	}

	private static MaterialAndMapping createListMaterialCore(String headline, String materialName,
			RelationType relationType, ListTemplate templateInfo,
			KnowledgeNode mainKnowledge, KnowledgeElement mainTerm) {
		var partRelations = getWantedRelationsFromRelated(mainKnowledge, relationType);
		var mainId = mainTerm.getId();
		var partNames = partRelations.stream().filter(it -> it.getToId().equals(mainId))
									 .map(it -> it.getFrom().getContent()).toList();
		if (partNames.isEmpty()) {
			return null;
		}
		var partListMaterial = new ListMaterial(headline, partNames);
		partListMaterial.setTerm(mainTerm.getContent());
		partListMaterial.setTemplateInfo(templateInfo);
		partListMaterial.setTermId(mainTerm.getId());
		partListMaterial.setName(materialName);
		partListMaterial.setStructureId(mainTerm.getStructureId());
		partListMaterial.setValues(Map.of("term", mainTerm.getContent()));
		var mapping = new MaterialMappingEntry(partListMaterial);
		mapping.add(mainTerm);
		mapping.addAll(partRelations.stream().filter(it -> it.getToId().equals(mainId)).map(Relation::getFrom)
									.toArray(KnowledgeElement[]::new));
		return new MaterialAndMapping(partListMaterial, mapping);
	}

	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge, String masterKeyword) {
		return knowledge.stream().filter(it -> it.getMainElement().hasType(KnowledgeType.TERM)
											   && it.getMainElement().getContent().contains(masterKeyword))
						.findFirst()
						.orElseThrow();
	}

	private static KnowledgeNode getMainKnowledge(Set<KnowledgeNode> knowledge) {
		return knowledge.stream().filter(it -> it.getMainElement().hasType(KnowledgeType.TERM))
						.findFirst().orElseThrow();
	}

	private void changed() {
		setUnchanged(false);
	}

	private void processGoals(List<ContentGoal> goals) {
		for (var goal : goals) {
			var expression = goal.getExpression();
			var masterKeyword = goal.getMasterKeyword();
			var topics = goal.getContent().stream().map(ContentTarget::getTopic).toList();
			try {
				var knowledge = loadKnowledgeForStructure(masterKeyword, topics);
				if (knowledge.isEmpty()) {
					logger.info("No knowledge found for MasterKeyword {}", masterKeyword);
					continue;
				}
				knowledge.forEach(it -> it.setGoal(goal));
				createMaterialForGoal(expression, knowledge);
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
	 */
	private void createMaterialForGoal(ContentGoalExpression expression, Set<KnowledgeNode> knowledge) {
		var materialAndMapping = switch (expression) {
			case FIRST_LOOK -> materialForFirstLook(knowledge);
			case KNOW -> materialForKnow(knowledge);
			case USE -> materialForUse(knowledge);
			case TRANSLATE -> materialForTranslate(knowledge);
			case CREATE -> materialForCreate(knowledge);
			case COMMENT -> materialForComment(knowledge);
			case CONTROL -> materialForControl(knowledge);
		};
		var material = materialAndMapping.stream().map(MaterialAndMapping::material).toList();
		var mapping = materialAndMapping.stream().map(MaterialAndMapping::mapping).toList();
		output.addMaterial(material);
		output.addMapping(mapping);
	}

	private List<MaterialAndMapping> materialForComment(Set<KnowledgeNode> knowledge) {
		return materialForCreate(knowledge);
	}

	private List<MaterialAndMapping> materialForCreate(Set<KnowledgeNode> knowledge) {
		return materialForControl(knowledge);
	}

	private List<MaterialAndMapping> materialForControl(Set<KnowledgeNode> knowledge) {
		return materialForUse(knowledge);
	}

	private List<MaterialAndMapping> materialForUse(Set<KnowledgeNode> knowledge) {
		var materials = materialForTranslate(knowledge);
		return materials;
	}

	private List<MaterialAndMapping> materialForTranslate(Set<KnowledgeNode> knowledge) {
		return materialForKnow(knowledge);
	}

	private List<MaterialAndMapping> materialForKnow(Set<KnowledgeNode> knowledge) {
		if (knowledge.isEmpty()) {
			return List.of();
		}
		final var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN);
		var materials = materialForFirstLook(knowledge);
		try {
			materials.addAll(createProofs(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No Proof found for {}", masterKeyword);
		}
		try {
			materials.addAll(createExamples(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No Example found for {}", masterKeyword);
		}
		try {
			materials.addAll(createCode(knowledge));
		} catch (NoTemplateInfoException | NoSuchElementException e) {
			logger.info("No Code found for {}", masterKeyword);
		}
		try {
			materials.addAll(createTexts(knowledge));
		} catch (NoSuchElementException e) {
			logger.info("No Text found for {}", masterKeyword);
		}
		if (materials.isEmpty()) {
			logger.info("No KNOW Material found for {}", masterKeyword);
		}
		return materials;
	}

	private List<MaterialAndMapping> materialForFirstLook(Set<KnowledgeNode> knowledge)
			throws NoSuchElementException {
		if (knowledge.isEmpty()) {
			return List.of();
		}
		final var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN);
		List<MaterialAndMapping> materials = createDefinitionsSave(knowledge, masterKeyword);
		materials.addAll(createListsSave(knowledge, masterKeyword));
		try {
			var synonyms = createSynonyms(knowledge);
			if (synonyms != null) {
				materials.add(synonyms);
			}
		} catch (NoSuchElementException e) {
			logger.info("No Synonym found for {}",
					knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN));
		}
		try {
			var acronyms = createAcronyms(knowledge);
			if (acronyms != null) {
				materials.add(acronyms);
			}
		} catch (NoSuchElementException e) {
			logger.info("No Acronym found for {}",
					knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElse(UNKNOWN));
		}
		//materials.add(createWikisWithExistingMaterial(knowledge, materials));
		if (materials.isEmpty()) {
			logger.info("No FIRST_LOOK Material created for {}",
					knowledge.stream().findFirst().orElseThrow().getGoal().orElseThrow());
		}
		return materials;
	}

	private List<MaterialAndMapping> createDefinitionsSave(Set<KnowledgeNode> knowledge, String masterKeyword) {
		try {
			return createDefinitions(knowledge);
		} catch (NoSuchElementException | IllegalArgumentException e) {
			logger.info("No Definition found for {}", masterKeyword);
		}
		return new LinkedList<>();
	}

	/**
	 * Create Definition Materials for a KnowledgeNode
	 *
	 * @param knowledge KnowledgeNodes to create Materials for
	 * @return List of Materials and Mappings that are Definitions
	 * @throws NoTemplateInfoException  if no Definition Template is found
	 * @throws NoSuchElementException   if no KnowledgeNode is found that has a Term as mainElement
	 * @throws IllegalArgumentException if the Knowledge is empty
	 */
	private List<MaterialAndMapping> createDefinitions(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException, IllegalArgumentException {
		if (knowledge.isEmpty()) {
			throw new IllegalArgumentException("Knowledge is empty");
		}
		var templateInfo = getBasicTemplateInfo(DefinitionTemplate.class);
		var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElseThrow();
		var mainKnowledge = getMainKnowledge(knowledge, masterKeyword);
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

	private MaterialAndMapping createWikisWithExistingMaterial(Set<KnowledgeNode> knowledge,
			List<MaterialAndMapping> existingMaterials) {
		// var material = new WikiPageMaterial();
		// var mapping = new MaterialMappingEntry(material);
		return null;//new MaterialAndMapping(material, mapping);
	}

	private List<MaterialAndMapping> createListsSave(Set<KnowledgeNode> knowledge, String masterKeyword) {
		try {
			return createLists(knowledge);
		} catch (NoSuchElementException e) {
			logger.info("No List found for {}", masterKeyword);
			return new LinkedList<>();
		}
	}

	/**
	 * Create a Synonym Material for a KnowledgeNode with Synonyms
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with synonyms
	 * @throws NoTemplateInfoException if no Synonym Template is found
	 * @throws NoSuchElementException  if no KnowledgeElement is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createLists(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		ListTemplate templateInfo = getBasicTemplateInfo(ListTemplate.class);
		var mainKnowledge = getMainKnowledge(knowledge);
		var mainTerm = mainKnowledge.getMainElement();
		var back = new ArrayList<MaterialAndMapping>();
		var newList = createListMaterialCore("Besteht aus", "Liste " + mainTerm.getContent() + " besteht aus",
				RelationType.PART_OF, templateInfo, mainKnowledge, mainTerm);
		if (newList != null) {
			back.add(newList);
		}

		newList = createListMaterialCore("NUTZT", "Liste " + mainTerm.getContent() + " nutzt", RelationType.IS_USED_BY,
				templateInfo, mainKnowledge, mainTerm);
		if (newList != null) {
			back.add(newList);
		}
		return back;
	}

	/**
	 * Create a List Material for a KnowledgeNode with Synonyms
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with synonyms and a mapping. If no synonyms are found, null is returned
	 * @throws NoTemplateInfoException if no Synonym Template is found
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */

	private MaterialAndMapping createSynonyms(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(SynonymTemplate.class);
		var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElseThrow();
		var mainKnowledge = getMainKnowledge(knowledge, masterKeyword);
		var mainTerm = mainKnowledge.getMainElement();
		List<String> synonyms = new LinkedList<>();
		List<KnowledgeElement> synonymsElements = new LinkedList<>();

		var mainId = mainTerm.getId();
		var symRelations = getWantedRelationsFromRelated(mainKnowledge, RelationType.IS_SYNONYM_FOR);
		collectElementsWithId(synonyms, symRelations, mainId, synonymsElements);

		Material material = new SynonymMaterial(synonyms, false, templateInfo, mainTerm);
		material.setName("Synonyme für " + mainTerm.getContent());
		MaterialMappingEntry mapping = new MaterialMappingEntry(material);
		mapping.add(mainTerm);
		synonymsElements.forEach(mapping::add);
		material.setValues(Map.of("term", mainTerm.getContent()));
		return new MaterialAndMapping(material, mapping);
	}

	/**
	 * Collect all Elements with a given id from a Set of Relations.
	 * Compares the toId with given id
	 *
	 * @param names     List to collect the names in
	 * @param relations Relations to search in
	 * @param id        id to search for
	 * @param elements  List to collect the elements in
	 */
	private void collectElementsWithId(List<String> names, Set<Relation> relations, String id,
			List<KnowledgeElement> elements) {
		relations.forEach(it -> {
			if (it.getToId().equals(id)) {
				var synonymElement = it.getFrom();
				names.add(synonymElement.getContent());
				elements.add(synonymElement);
			}
		});

	}

	/**
	 * Create a List Material for a KnowledgeNode with Acronyms
	 *
	 * @param knowledge KnowledgeNode to create Material for
	 * @return A material with acronyms and a mapping. If no acronyms are found, null is returned
	 * @throws NoTemplateInfoException if no Acronym Template is found
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private MaterialAndMapping createAcronyms(Set<KnowledgeNode> knowledge) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(AcronymTemplate.class);
		var masterKeyword = knowledge.stream().findFirst().orElseThrow().getMasterKeyWord().orElseThrow();
		var mainKnowledge = getMainKnowledge(knowledge, masterKeyword);
		var mainTerm = mainKnowledge.getMainElement();
		List<String> acronyms = new LinkedList<>();
		List<KnowledgeElement> acronymsElements = new LinkedList<>();

		var mainId = mainTerm.getId();
		var acryRelations = getWantedRelationsFromRelated(mainKnowledge, RelationType.IS_ACRONYM_FOR);
		collectElementsWithId(acronyms, acryRelations, mainId, acronymsElements);
		Material material = new AcronymMaterial(acronyms, false, templateInfo, mainTerm);
		String name = "Akronyme für " + mainTerm.getContent();
		material.setName(name);
		MaterialMappingEntry mapping = new MaterialMappingEntry(material);
		mapping.add(mainTerm);
		acronymsElements.forEach(mapping::add);
		material.setValues(Map.of("term", mainTerm.getContent()));
		return new MaterialAndMapping(material, mapping);
	}

	/**
	 * Create a List of Materials of type Text for a KnowledgeNode.
	 *
	 * @param knowledgeNode KnowledgeNode to create Material for
	 * @return A material with texts and a mapping. If no texts are found, empty list is returned.
	 * @throws NoTemplateInfoException if no Text Template is found
	 * @throws NoSuchElementException  if no TERM is found that is the mainElement of the KnowledgeNode
	 */
	private List<MaterialAndMapping> createTexts(Set<KnowledgeNode> knowledgeNode) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(TextTemplate.class);
		var mainKnowledge = knowledgeNode.stream()
										 .filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM))
										 .findFirst().orElseThrow();
		var mainTerm = mainKnowledge.getMainElement();
		List<MaterialAndMapping> back = new ArrayList<>();
		var textRelations = Arrays.stream(mainKnowledge.getRelations())
								  .filter(it -> it.getType().equals(RelationType.HAS));
		textRelations.forEach(it -> {
			var textId = it.getFromId();
			try {
				var textElement = Arrays.stream(mainKnowledge.getRelatedElements())
										.filter(elem -> elem.getId().equals(textId)).findFirst().orElseThrow();
				Material textMaterial = new Material(MaterialType.WIKI, mainTerm);
				textMaterial.setName(mainTerm.getContent());
				textMaterial.setTemplateInfo(templateInfo);
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

	/**
	 * Create a Material that contains Code
	 *
	 * @param knowledgeNode The KnowledgeNode to create the Material for
	 * @return A List of Materials with Code
	 * @throws NoSuchElementException  If no Code is found
	 * @throws NoTemplateInfoException If no TemplateInfo for code is found
	 */
	private List<MaterialAndMapping> createCode(Set<KnowledgeNode> knowledgeNode) throws NoTemplateInfoException,
			NoSuchElementException {
		var templateInfo = getBasicTemplateInfo(CodeTemplate.class);
		List<MaterialAndMapping> back = new ArrayList<>();
		var codeKnowledgeNodes = knowledgeNode.stream()
											  .filter(it -> it.getMainElement().getType().equals(KnowledgeType.CODE))
											  .toList();
		if (codeKnowledgeNodes.isEmpty()) {
			throw new NoSuchElementException("No code found");
		}
		for (var knowledge : codeKnowledgeNodes) {
			var codeElement = (Code) knowledge.getMainElement();
			Material codeMaterial = new CodeMaterial(codeElement.getLanguage(), codeElement.getCodeLines(),
					codeElement.getCaption(),
					codeElement);
			codeMaterial.setTemplateInfo(templateInfo);
			MaterialMappingEntry mapping = new MaterialMappingEntry(codeMaterial);
			mapping.add(codeElement);
			back.add(new MaterialAndMapping(codeMaterial, mapping));
		}
		return back;
	}

	private List<MaterialAndMapping> createExamples(Set<KnowledgeNode> knowledge) {
		var templateInfo = getBasicTemplateInfo(ExampleTemplate.class);
		var mainKnowledge = knowledge.stream()
									 .filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM))
									 .findFirst().orElseThrow();
		List<MaterialAndMapping> back = new ArrayList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var relations = getWantedRelationsFromRelated(mainKnowledge, RelationType.EXAMPLE_FOR);
		relations.forEach(it -> {
			var mainId = it.getFromId();
			try {
				var example = Arrays.stream(mainKnowledge.getRelatedElements())
									.filter(elem -> elem.getId().equals(mainId)).findFirst().orElseThrow();
				String name = getUniqueMaterialName(back, "Beispiel " + mainTerm.getContent(), mainId);
				var values = Map.of("term", mainTerm.getContent(), "example", example.getContent());
				var materialAndMapping = new MaterialCreator().createExampleMaterial(example, mainTerm, name,
						templateInfo,
						values, example);
				back.add(materialAndMapping);
			} catch (Exception ignored) {
				logger.warn("No example found for {}", mainTerm.getContent());
			}
		});
		return back;
	}

	private List<MaterialAndMapping> createProofs(Set<KnowledgeNode> knowledge) {
		var templateInfo = getBasicTemplateInfo(ProofTemplate.class);
		var mainKnowledge = knowledge.stream()
									 .filter(it -> it.getMainElement().getType().equals(KnowledgeType.TERM))
									 .findFirst().orElseThrow();
		List<MaterialAndMapping> back = new ArrayList<>();
		var mainTerm = mainKnowledge.getMainElement();
		var relations = getWantedRelationsFromRelated(mainKnowledge, RelationType.PROOFS);
		relations.forEach(it -> {
			var proofId = it.getFromId();
			try {
				var proofElement = Arrays.stream(mainKnowledge.getRelatedElements())
										 .filter(elem -> elem.getId().equals(proofId)).findFirst().orElseThrow();
				var name = getUniqueMaterialName(back, "Beweis " + mainTerm.getContent(), proofId);
				var values = Map.of("term", mainTerm.getContent(), "proof", proofElement.getContent());
				var materialAndMapping = new MaterialCreator().createProofMaterial(proofElement, mainTerm, name,
						templateInfo,
						values, proofElement);
				back.add(materialAndMapping);
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
	private <T extends TemplateInfo> T getBasicTemplateInfo(Class<T> templateInfoClass) throws
			NoTemplateInfoException {
		return (T) basicTemplateInfo.stream()
									.filter(it -> templateInfoClass.equals(it.getClass()))
									.findFirst().orElseThrow(() -> new NoTemplateInfoException(
						String.format("No Template %s found", templateInfoClass.getName())));
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId, Collection<String> target) {
		return loadKnowledgeForStructure(structureId, target.toArray(new String[0]));
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId, String... targets) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		var back = new HashSet<>(loadKnowledgeForStructure(structureId));
		for (String target : targets) {
			if (target == null) {
				continue;
			}
			back.addAll(loadKnowledgeForStructure(target));
		}
		return back;
	}

	private Set<KnowledgeNode> loadKnowledgeForStructure(String structureId) {
		if (structureId == null) {
			return Collections.emptySet();
		}
		return model.getKnowledgeNodesIncludingSimilarFor(structureId);
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

	/**
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	public boolean isReady() {
		return templateSet != null && model != null && plan != null;
	}

	/**
	 * checks if the input is unchanged
	 *
	 * @return {@literal true} if the input is unchanged, {@literal false} if not
	 */
	private boolean isUnchanged() {
		return unchanged;
	}

	private void setUnchanged(boolean unchanged) {
		this.unchanged = unchanged;
	}

	/**
	 * Sets the templateSet.
	 * Will change the inner state of the generator.
	 *
	 * @param templateSet the templateSet to set
	 */
	public void setTemplateSet(TemplateSet templateSet) {
		this.templateSet = templateSet;
		changed();
	}

	/**
	 * Sets the model.
	 * Will change the inner state of the generator.
	 *
	 * @param model the model to set
	 */
	public void setModel(KnowledgeModel model) {
		this.model = model;
		changed();
	}

	/**
	 * Sets the plan
	 * Will change the inner state of the generator.
	 *
	 * @param plan the plan to set
	 */
	public void setPlan(CoursePlan plan) {
		this.plan = plan;
		changed();
	}

	/**
	 * Sets the basic templateInfo.
	 * Will change the inner state of the generator.
	 *
	 * @param basicTemplateInfo the basic templateInfo to set
	 */
	public void setBasicTemplateInfo(Set<BasicTemplate> basicTemplateInfo) {
		this.basicTemplateInfo.clear();
		this.basicTemplateInfo.addAll(basicTemplateInfo);
		changed();
	}
//endregion
}
