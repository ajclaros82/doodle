package com.ajclaros.doodle.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.ajclaros.doodle.util.JsonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonUtilsTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void readTextOrNull_returnsNull_whenFieldMissing() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":1}");
		assertNull(readTextOrNull(node, "missing"));
	}

	@Test
	void readTextOrNull_returnsNull_whenFieldIsNull() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":null}");
		assertNull(readTextOrNull(node, "a"));
	}

	@Test
	void readTextOrNull_returnsText_whenFieldPresent() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":\"hello\"}");
		assertEquals("hello", readTextOrNull(node, "a"));
	}

	@Test
	void readNonBlankText_returnsNull_whenBlankOrMissing() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":\"\",\"b\":\"   \"}");

		assertNull(readNonBlankText(node, "missing"));
		assertNull(readNonBlankText(node, "a"));
		assertNull(readNonBlankText(node, "b"));
	}

	@Test
	void readNonBlankText_returnsText_whenNotBlank() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":\" ok \"}");
		assertEquals(" ok ", readNonBlankText(node, "a"));
	}

	@Test
	void readLongOrNull_returnsNull_whenMissingOrNull() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":null}");

		assertNull(readLongOrNull(node, "missing"));
		assertNull(readLongOrNull(node, "a"));
	}

	@Test
	void readLongOrNull_readsNumber() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":123}");
		assertEquals(123L, readLongOrNull(node, "a"));
	}

	@Test
	void readBooleanOrNull_returnsNull_whenMissingOrNull() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":null}");

		assertNull(readBooleanOrNull(node, "missing"));
		assertNull(readBooleanOrNull(node, "a"));
	}

	@Test
	void readBooleanOrNull_readsBoolean() throws Exception {
		final JsonNode node = this.objectMapper.readTree("{\"a\":true,\"b\":false}");
		assertEquals(Boolean.TRUE, readBooleanOrNull(node, "a"));
		assertEquals(Boolean.FALSE, readBooleanOrNull(node, "b"));
	}
}
