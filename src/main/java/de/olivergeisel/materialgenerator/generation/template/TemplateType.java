package de.olivergeisel.materialgenerator.generation.template;

import javax.persistence.Embeddable;

@Embeddable
public record TemplateType(String type) {

	public static final TemplateType DEFINITION = new TemplateType("DEFINITION");
	public static final TemplateType EXERCISE = new TemplateType("EXERCISE");
	public static final TemplateType SOLUTION = new TemplateType("SOLUTION");
	public static final TemplateType TEXT = new TemplateType("TEXT");
	public static final TemplateType EXAMPLE = new TemplateType("EXAMPLE");

	public TemplateType() {
		this("TEXT");
	}

	public static TemplateType valueOf(String typeString) {
		return switch (typeString) {
			case "DEFINITION" -> DEFINITION;
			case "EXERCISE" -> EXERCISE;
			case "SOLUTION" -> SOLUTION;
			case "TEXT" -> TEXT;
			case "EXAMPLE" -> EXAMPLE;
			default -> new TemplateType(typeString);
		};
	}
}
