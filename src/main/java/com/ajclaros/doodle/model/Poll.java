package com.ajclaros.doodle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Minimal persisted representation of a poll from sample_data.json.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "polls")
public class Poll {

	@Id
	private String id;
	private Long initiated;
	private String title;
	private Initiator initiator;

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
