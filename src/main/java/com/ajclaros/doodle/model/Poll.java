package com.ajclaros.doodle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Persisted representation of a poll from sample_data.json.
 * <p>
 * We keep the main searchable fields strongly-typed, and store the rest of the
 * JSON payload as-is so the full document can be retrieved later.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "polls")
@CompoundIndexes({@CompoundIndex(name = "idx_initiator_name", def = "{'initiator.name': 1}")})
public class Poll {

	@Id
	private String id;

	@Indexed(name = "idx_initiated")
	private Long initiated;

	@Indexed(name = "idx_title")
	private String title;

	private Initiator initiator;

	/**
	 * Raw poll JSON fields (everything else besides id/initiated/title/initiator).
	 * <p>
	 * This lets us persist and later retrieve the full document without modelling
	 * the complete Doodle schema.
	 */
	private String raw;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Initiator {
		private String name;
		private String email;
		private Boolean notify;
	}
}
