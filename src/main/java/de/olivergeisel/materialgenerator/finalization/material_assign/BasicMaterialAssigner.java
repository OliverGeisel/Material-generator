package de.olivergeisel.materialgenerator.finalization.material_assign;


import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Set;

/**
 * Assign material to a MaterialOrderCollection. This is the basic implementation of the {@link MaterialAssigner}.
 * All this Assigner does is to call the assignMaterial method of the {@link MaterialOrderCollection}.
 * So the MaterialOrderCollection itself decides how to assign the material.
 */
public class BasicMaterialAssigner extends MaterialAssigner {

	public BasicMaterialAssigner(Set<Material> materials) {
		super(materials);
	}

	/**
	 * Assign a material to a {@link MaterialOrderCollection}.
	 *
	 * @param part the part to assign the material to
	 * @return true if the material was assigned, false if not
	 * @see MaterialOrderCollection#assignMaterial(Set)
	 */
	@Override
	public boolean assign(MaterialOrderCollection part) {
		return part.assignMaterial(materialMap.keySet());
	}

	/**
	 * Assign a material to a specific {@link MaterialOrderCollection}.
	 *
	 * @param material the material to assign
	 * @param part     the part to assign the material to
	 * @return true if the material was assigned, false if not
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		if (!materialMap.containsKey(material)) {
			throw new IllegalArgumentException("Material not found in Assigner!");
		}
		materialMap.get(material).setAssigned(true);
		return false;
	}
}
