package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import de.olivergeisel.materialgenerator.core.knowledge.metamodel.KnowledgeModel;

import java.util.Set;

public interface Generator {

 Set<MaterialAndMapping> generate(KnowledgeModel model, CoursePlan plan, Set<MaterialTemplate> templates);
}
