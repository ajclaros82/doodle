package com.ajclaros.doodle.service.importing;

import com.ajclaros.doodle.model.Poll;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonPollParserTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JsonPollParser parser = new JsonPollParser(this.objectMapper);

	@Test
	void parse_requiresArray() {
		final JsonNode notArray = JsonNodeFactory.instance.objectNode();
		assertThrows(IllegalStateException.class, () -> this.parser.parse(notArray));
	}

	@Test
	void parse_skipsEntriesWithMissingId_andMapsFieldsAndRaw() throws Exception {
		final String json = "["
				+ "{\"id\":\"p1\",\"initiated\":1000,\"title\":\"First\",\"initiator\":{\"name\":\"John\"}},"
				+ "{\"initiated\":2000,\"title\":\"Missing id should be skipped\"}" + "]";
		final JsonNode root = this.objectMapper.readTree(json);

		final List<Poll> polls = this.parser.parse(root);

		assertEquals(1, polls.size());
		final Poll p1 = polls.getFirst();
		assertEquals("p1", p1.getId());
		assertEquals(1000L, p1.getInitiated());
		assertEquals("First", p1.getTitle());
		assertNotNull(p1.getInitiator());
		assertEquals("John", p1.getInitiator().getName());

		assertNotNull(p1.getRaw());
		assertEquals("p1", p1.getRaw().get("id"));
		assertEquals("First", p1.getRaw().get("title"));
	}

	@Test
	void parse_acceptsNullInitiator() throws Exception {
		final JsonNode root = this.objectMapper.readTree("[{\"id\":\"p1\",\"title\":\"T\"}]");

		final List<Poll> polls = this.parser.parse(root);

		assertEquals(1, polls.size());
		assertNull(polls.getFirst().getInitiator());
	}
}
