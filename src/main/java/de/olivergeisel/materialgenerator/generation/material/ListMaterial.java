package de.olivergeisel.materialgenerator.generation.material;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ListMaterial extends Material {

	@ElementCollection
	private List<String> entries = new LinkedList<>();
	private String headline;
	private boolean numerated;

	public ListMaterial() {
		super(MaterialType.WIKI);
	}

	protected ListMaterial(MaterialType type) {
		super(type);
	}

	public ListMaterial(String headline, Collection<String> entries, boolean numerated) {
		super(MaterialType.WIKI);
		this.entries.addAll(entries);
		this.headline = headline;
		this.numerated = numerated;
	}

	public ListMaterial(String headline, Collection<String> entries) {
		super(MaterialType.WIKI);
		this.entries.addAll(entries);
		this.headline = headline;
		this.numerated = false;
	}

	public ListMaterial(String headline) {
		super(MaterialType.WIKI);
		this.headline = headline;
		this.numerated = false;
	}

	//region setter/getter
	public List<String> getEntries() {
		return entries;
	}

	public void setEntries(List<String> entries) {
		this.entries = entries;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public boolean isNumerated() {
		return numerated;
	}

	public void setNumerated(boolean numerated) {
		this.numerated = numerated;
	}
//endregion


}