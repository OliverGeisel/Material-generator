package de.olivergeisel.materialgenerator.generation.material;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class WikiPageMaterial extends ComplexMaterial {

	@ManyToOne
	private Material       definition;
	@ManyToOne
	private Material       synonym;
	@ManyToOne
	private Material       acronym;
	@ManyToMany
	private List<Material> texts;
	@ManyToOne
	private Material       example;

	public WikiPageMaterial(List<Material> parts) {
		super(MaterialType.WIKI, parts);
	}

	protected WikiPageMaterial() {
		super(MaterialType.WIKI);
	}
}
