package de.olivergeisel.materialgenerator.generation.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GeneratorOutput {
	private final Set<Material> allMaterial = new HashSet<>();
	private final Set<MaterialMappingEntry> allMappings = new HashSet<>();

	public void add(MaterialAndMapping materialAndMapping) {
		addMapping(materialAndMapping.mapping());
		addMaterial(materialAndMapping.material());
	}

	public void addMapping(MaterialMappingEntry mapping) {
		this.allMappings.add(mapping);
	}

	public void addMaterial(Material... materials) {
		for (var newMaterial : materials) {
			addMaterial(newMaterial);
		}
	}

	public void addMaterial(Collection<Material> materials) {
		for (var newMaterial : materials) {
			addMaterial(newMaterial);
		}
	}

	public void addMaterial(Material material) {
		this.allMaterial.add(material);
	}

	public void removeMapping(MaterialMappingEntry mapping) {
		allMappings.remove(mapping);
	}

	public boolean removeMaterial(Material material) {
		return this.allMaterial.remove(material);
	}

	//region setter/getter
	public Set<MaterialAndMapping> getMaterialAndMapping() {
		Set<MaterialAndMapping> result = new HashSet<>();
		for (var material : allMaterial) {
			result.add(new MaterialAndMapping(material, allMappings.stream().filter(m -> m.getMaterial() == material).findFirst().orElseThrow()));
		}
		return result;
	}

	public Set<MaterialMappingEntry> getAllMappings() {
		return Collections.unmodifiableSet(allMappings);
	}

	public Set<Material> getAllMaterial() {
		return Collections.unmodifiableSet(allMaterial);
	}
//endregion
}
