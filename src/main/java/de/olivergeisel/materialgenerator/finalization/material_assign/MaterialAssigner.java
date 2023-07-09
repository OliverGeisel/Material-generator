package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Map;
import java.util.Set;

public abstract class MaterialAssigner {

	protected final Map<Material, MaterialStatus> materialMap;

	protected MaterialAssigner(Set<Material> materials) {
		materialMap = new java.util.HashMap<>();
		for (Material material : materials) {
			materialMap.put(material, new MaterialStatus());
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

	public abstract boolean assign(MaterialOrderCollection part);

	public abstract boolean assign(Material material, MaterialOrderCollection part);

	//region setter/getter
	protected void setAssigned(Material material) {
		materialMap.get(material).assigned = true;
	}
//endregion

	protected static class MaterialStatus {
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
