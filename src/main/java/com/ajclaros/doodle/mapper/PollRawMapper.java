package com.ajclaros.doodle.mapper;

import com.ajclaros.doodle.model.Poll;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Maps a persisted {@link Poll} to its stored raw JSON payload.
 */
@Service
@RequiredArgsConstructor
public class PollRawMapper {

	private final ObjectMapper objectMapper;

	public List<JsonNode> toRawJson(final List<Poll> polls) {
		return polls.stream().map(this::toRawJson).toList();
	}

	public JsonNode toRawJson(final Poll poll) {
		final String raw = poll.getRaw();
		if (raw == null || raw.isBlank()) {
			return this.objectMapper.nullNode();
		}
		try {
			return this.objectMapper.readTree(raw);
		} catch (final Exception e) {
			// If the stored raw is not valid JSON for some reason, return it as a string
			// node.
			return this.objectMapper.getNodeFactory().textNode(raw);
		}
	}
}
