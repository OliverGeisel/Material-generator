package de.olivergeisel.materialgenerator.generation.generator;

import de.olivergeisel.materialgenerator.generation.material.Material;
import de.olivergeisel.materialgenerator.generation.material.MaterialAndMapping;
import de.olivergeisel.materialgenerator.generation.material.MaterialMappingEntry;

import java.util.*;

public class GeneratorOutput {
	private final List<Material>             allMaterial = new LinkedList<>();
	private final List<MaterialMappingEntry> allMappings = new LinkedList<>();

	public void add(MaterialAndMapping materialAndMapping) {
		addMapping(materialAndMapping.mapping());
		addMaterial(materialAndMapping.material());
	}

	public void addAll(Collection<MaterialAndMapping> materialAndMappings) {
		for (var materialAndMapping : materialAndMappings) {
			add(materialAndMapping);
		}
	}

	public void addMapping(MaterialMappingEntry mapping) {
		this.allMappings.add(mapping);
	}

	public void addMapping(MaterialMappingEntry... mappings) {
		for (var newMapping : mappings) {
			addMapping(newMapping);
		}
	}

	public void addMapping(Collection<MaterialMappingEntry> mappings) {
		for (var newMapping : mappings) {
			addMapping(newMapping);
		}
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
			result.add(new MaterialAndMapping(material,
											  allMappings.stream().filter(m -> m.getMaterial() == material).findFirst()
														 .orElseThrow()));
		}
		return result;
	}

	public List<MaterialMappingEntry> getAllMappings() {
		return allMappings;
	}

	public List<Material> getAllMaterial() {
		return allMaterial;
	}
//endregion
}
