package com.ajclaros.doodle.service.importing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClasspathJsonPollSourceTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ClasspathJsonPollSource source = new ClasspathJsonPollSource(this.objectMapper);

	@Test
	void load_readsSampleDataJson_fromClasspath() {
		final JsonNode root = this.source.load();

		assertNotNull(root);
		assertTrue(root.isArray());
		assertEquals(3, root.size());
		assertEquals("p1", root.get(0).path("id").asText());
	}
}
