package de.olivergeisel.materialgenerator.finalization.material_assign;

import de.olivergeisel.materialgenerator.finalization.parts.ChapterOrder;
import de.olivergeisel.materialgenerator.finalization.parts.GroupOrder;
import de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection;
import de.olivergeisel.materialgenerator.generation.material.Material;

import java.util.Set;

/**
 * Assign materials to a MaterialOrderCollection. This assigner is strict. It only assigns materials to a part if one
 * of the first n alias match the requirements. n is set by {@literal maxAlias}. If no alias is set, the part is not
 * assigned.
 */
public class StrictMaterialAssigner extends MaterialAssigner {

	private int maxAlias;

	public StrictMaterialAssigner(Set<Material> materials) {
		super(materials);
		maxAlias = 1;
	}

	public StrictMaterialAssigner(Set<Material> materials, int maxAlias) {
		super(materials);
		this.maxAlias = maxAlias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(MaterialOrderCollection part) {
		if (part instanceof ChapterOrder || part instanceof GroupOrder)
			return false;
		if (part.getAlias().isEmpty())
			return false;
		var aliase = part.getAlias().stream().limit(maxAlias);
		var back = false;
		for (var alias : aliase.toList()) {
			for (var material : getUnassignedMaterials()) {
				if (material.getStructureId().contains(alias)) {
					part.assign(material);
					setAssigned(material);
					back = true;
				}
			}
		}
		return back;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean assign(Material material, MaterialOrderCollection part) {
		return false;
	}

	//region setter/getter
	public int getMaxAlias() {
		return maxAlias;
	}

	public void setMaxAlias(int maxAlias) {
		if (maxAlias < 1)
			throw new IllegalArgumentException("maxAlias must be greater than 0!");
		this.maxAlias = maxAlias;
	}
//endregion

}
