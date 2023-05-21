package de.olivergeisel.materialgenerator.generation.template;

import javax.persistence.*;
import java.util.UUID;

@Entity

public class TemplateSet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private UUID id;

	public UUID getId() {
		return id;
	}


}
