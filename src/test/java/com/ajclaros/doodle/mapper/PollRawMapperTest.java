package com.ajclaros.doodle.mapper;

import com.ajclaros.doodle.model.Poll;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PollRawMapperTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final PollRawMapper mapper = new PollRawMapper(this.objectMapper);

	@Test
	void toRawJson_whenRawIsValidJson_returnsObjectNode() {
		final Poll poll = Poll.builder().id("p1").raw("{\"id\":\"p1\",\"x\":1}").build();

		final var node = this.mapper.toRawJson(poll);

		assertTrue(node.isObject());
		assertEquals("p1", node.path("id").asText());
		assertEquals(1, node.path("x").asInt());
	}

	@Test
	void toRawJson_whenRawIsBlank_returnsNullNode() {
		final Poll poll = Poll.builder().id("p1").raw(" ").build();

		final var node = this.mapper.toRawJson(poll);

		assertTrue(node.isNull());
	}

	@Test
	void toRawJson_whenRawIsNotJson_returnsTextNode() {
		final Poll poll = Poll.builder().id("p1").raw("not-json").build();

		final var node = this.mapper.toRawJson(poll);

		assertTrue(node.isTextual());
		assertEquals("not-json", node.asText());
	}
}
