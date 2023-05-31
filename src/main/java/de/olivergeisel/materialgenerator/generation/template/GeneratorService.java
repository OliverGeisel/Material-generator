package de.olivergeisel.materialgenerator.generation.template;

import de.olivergeisel.materialgenerator.core.knowledge.KnowledgeManagement;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneratorService {

	private final KnowledgeManagement knowledgeManagement;

	public GeneratorService(KnowledgeManagement knowledgeManagement) {
		this.knowledgeManagement = knowledgeManagement;
	}

	public Map<String, String> getPlain(String definition) {
		Map<String, String> back = new HashMap<>();
		List<Map<String, ?>> objects = knowledgeManagement.getNewModelAsJSON();
		for (var element : objects) {
			if (element.get("typ").equals("Definition") && ((String) element.get("id")).contains(definition)) {
				back.put("term", definition.split("-")[0]);
				back.put("definition", (String) element.get("content"));
				break;
			}
		}
		return back;
	}

//region getter / setter
	public Map<String, String> getIllustrated() {
		return null;
	}
//endregion

}
