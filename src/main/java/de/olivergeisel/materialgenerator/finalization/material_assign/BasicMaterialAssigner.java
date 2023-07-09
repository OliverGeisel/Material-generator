package de.olivergeisel.materialgenerator.finalization.material_assign;


import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Set;

/**
 * Assign material to a MaterialOrderCollection. This is the basic implementation of the {@link MaterialAssigner}.
 * All this Assigner does is to call the assignMaterial method of the {@link MaterialOrderCollection}.
 * So the MaterialOrderCollection itself decides how to assign the material. Only exception is when the mataerial was
 * already assigned. In this case the material is not assigned to a new part.
 *
 * @author Oliver Geisel
 * @version 1.0
 * @see MaterialOrderCollection#assignMaterial(Set)
 * @since 0.2.0
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
		part.assignMaterial(materialMap.keySet());
		return switch (part) {
			case ChapterOrder chapterOrder -> false;
			case GroupOrder groupOrder -> false;
			default -> {
				var res = part.assignMaterial(getUnassignedMaterials());
				setAssigned(res);
				yield !res.isEmpty();
			}
		};
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
		var back = part.assign(material);
		materialMap.get(material).setAssigned(true);
		return back;
	}
}
