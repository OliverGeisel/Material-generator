package de.olivergeisel.materialgenerator.core.course;

import javax.persistence.*;
import java.util.UUID;

/**
 * A part of a material order. A material order can be a complete course or a single material.
 *
 * @author Oliver Geisel
 * @version 1.0
 * @see de.olivergeisel.materialgenerator.generation.material.Material
 * @see de.olivergeisel.materialgenerator.finalization.parts.MaterialOrderCollection
 * @since 0.2.0
 */
@Entity
public abstract class MaterialOrderPart {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID   id;
	private String name;

	/**
	 * Find a part by its id. Must return null if not found.
	 *
	 * @param id id of the part
	 * @return the part or null if not found
	 */
	public abstract MaterialOrderPart find(UUID id);

//region setter/getter

	/**
	 * Check if all Parts match there relevance.
	 *
	 * @return true if all parts are valid
	 */
	public abstract boolean isValid();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getId() {
		return id;
	}
//endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MaterialOrderPart that)) return false;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "MaterialOrderPart{" + "name='" + name + ", id=" + id + '\'' + '}';
	}
}

