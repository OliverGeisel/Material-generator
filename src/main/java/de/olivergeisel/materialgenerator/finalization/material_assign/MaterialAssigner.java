package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assign materials to a MaterialOrderCollection.
 * <p>
 * Has internal states to hande assignment.
 * </p>
 *
 * @author Oliver Geisel
 * @version 1.0
 * @since 0.2.0
 */
public abstract class MaterialAssigner {

	protected final Map<Material, MaterialState> materialMap;

	protected MaterialAssigner(Set<Material> materials) {
		materialMap = new java.util.HashMap<>();
		for (Material material : materials) {
			materialMap.put(material, new MaterialState());
		}
	}

	public boolean hasMaterial() {
		return !materialMap.isEmpty();
	}

	public boolean hasUnassignedMaterials() {
		return materialMap.values().stream().anyMatch(it -> !it.isAssigned());
	}

	/**
	 * Check if a material is assigned.
	 *
	 * @param material material to check
	 * @return {@literal true} if assigned, otherwise {@literal false}
	 * @throws IllegalArgumentException if material is not in the map
	 */
	public boolean isAssigned(Material material) throws IllegalArgumentException {
		if (!materialMap.containsKey(material)) {
			throw new IllegalArgumentException();
		}
		return materialMap.get(material).isAssigned();
	}

	/**
	 * Set all given materials as assigned.
	 *
	 * @param materials materials to assign. If null it will return false;
	 * @return {@literal true} if at least one given material is true, otherwise false.
	 */
	public boolean setAssigned(Set<Material> materials) {
		if (materials == null) {
			return false;
		}
		var back = false;
		for (var material : materials) {
			if (!materialMap.containsKey(material)) {
				continue;
			}
			materialMap.get(material).setAssigned(true);
			back = true;
		}
		return back;
	}

	/**
	 * Try to assign all materials to a {@link MaterialOrderCollection}.
	 *
	 * @param part the part to assign the materials to
	 * @return {@literal true} if at least one material was assigned, otherwise false.
	 */
	public abstract boolean assign(MaterialOrderCollection part);

	/**
	 * Assign a specific material to a {@link MaterialOrderCollection}.
	 *
	 * @param material the material to assign. Must be in the material set of this assigner.
	 * @param part     the part to assign the material to
	 * @return {@literal true} if the material was assigned, otherwise false.
	 */
	public abstract boolean assign(Material material, MaterialOrderCollection part);

	//region setter/getter

	/**
	 * Get all materials that are not assigned.
	 *
	 * @return set of materials that are not assigned
	 */
	public Set<Material> getUnassignedMaterials() {
		return materialMap.entrySet().stream().filter(entry -> !entry.getValue().isAssigned()).map(Map.Entry::getKey)
						  .collect(Collectors.toSet());
	}

	/**
	 * Mark a material as assigned.
	 * if the material is not in the map, it will do nothing.
	 *
	 * @param material material to mark as assigned
	 */
	protected void setAssigned(Material material) {
		materialMap.computeIfPresent(material, (key, value) -> {
			value.assigned = true;
			return value;
		});
	}
	//endregion

	protected static class MaterialState {
		private boolean assigned = false;

		//region setter/getter
		public boolean isAssigned() {
			return assigned;
		}

		public void setAssigned(boolean assigned) {
			this.assigned = assigned;
		}
		//endregion
	}
}
