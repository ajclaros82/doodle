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
		final Object raw = poll.getRaw();
		if (raw == null) {
			return this.objectMapper.nullNode();
		}
		return this.objectMapper.valueToTree(raw);
	}
}
