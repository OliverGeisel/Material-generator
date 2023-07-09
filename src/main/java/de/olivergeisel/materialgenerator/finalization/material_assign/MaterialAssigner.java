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

	public boolean isAssigned(Material material) {
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

	public abstract boolean assign(MaterialOrderCollection part);

	public abstract boolean assign(Material material, MaterialOrderCollection part);

	//region setter/getter
	public Set<Material> getUnassignedMaterials() {
		return materialMap.entrySet().stream().filter(entry -> !entry.getValue().isAssigned()).map(Map.Entry::getKey)
						  .collect(Collectors.toSet());
	}

	protected void setAssigned(Material material) {
		materialMap.get(material).assigned = true;
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
