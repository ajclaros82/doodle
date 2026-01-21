package com.ajclaros.doodle.mapper;

import com.ajclaros.doodle.model.Poll;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PollRawMapperTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final PollRawMapper mapper = new PollRawMapper(this.objectMapper);

	@Test
	void toRawJson_whenRawIsMap_returnsObjectNode() {
		final LinkedHashMap<String, Object> raw = new LinkedHashMap<>();
		raw.put("id", "p1");
		raw.put("x", 1);

		final Poll poll = Poll.builder().id("p1").raw(raw).build();

		final var node = this.mapper.toRawJson(poll);

		assertTrue(node.isObject());
		assertEquals("p1", node.path("id").asText());
		assertEquals(1, node.path("x").asInt());
	}

	@Test
	void toRawJson_whenRawIsNull_returnsNullNode() {
		final Poll poll = Poll.builder().id("p1").raw(null).build();

		final var node = this.mapper.toRawJson(poll);

		assertTrue(node.isNull());
	}
}
