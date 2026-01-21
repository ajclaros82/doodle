package com.ajclaros.doodle.service;

import com.ajclaros.doodle.mapper.PollRawMapper;
import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PollQueryServiceTest {

	private final PollRepository pollRepository = mock(PollRepository.class);
	private final PollRawMapper pollRawMapper = mock(PollRawMapper.class);
	private final PollQueryService pollQueryService = new PollQueryService(this.pollRepository, this.pollRawMapper);

	@Test
	void findByInitiatorName_delegatesToRepositoryAndMapper() {
		final List<Poll> polls = List.of(poll("p1"));
		final List<JsonNode> mapped = List.of(rawNode("p1"));

		when(this.pollRepository.findByInitiatorNameContainingIgnoreCase(eq("john"))).thenReturn(polls);
		when(this.pollRawMapper.toRawJson(eq(polls))).thenReturn(mapped);

		final var result = this.pollQueryService.findByInitiatorName("john");

		assertEquals(mapped, result);
		verify(this.pollRepository).findByInitiatorNameContainingIgnoreCase("john");
		verify(this.pollRawMapper).toRawJson(polls);
		verifyNoMoreInteractions(this.pollRepository, this.pollRawMapper);
	}

	@Test
	void findByTitle_delegatesToRepositoryAndMapper() {
		final List<Poll> polls = List.of(poll("p1"));
		final List<JsonNode> mapped = List.of(rawNode("p1"));

		when(this.pollRepository.findByTitleContainingIgnoreCase(eq("meeting"))).thenReturn(polls);
		when(this.pollRawMapper.toRawJson(eq(polls))).thenReturn(mapped);

		final var result = this.pollQueryService.findByTitle("meeting");

		assertEquals(mapped, result);
		assertEquals("p1", result.getFirst().path("id").asText());

		verify(this.pollRepository).findByTitleContainingIgnoreCase("meeting");
		verify(this.pollRawMapper).toRawJson(polls);
		verifyNoMoreInteractions(this.pollRepository, this.pollRawMapper);
	}

	@Test
	void findInitiatedAfter_delegatesToRepositoryAndMapper() {
		final List<Poll> polls = List.of(poll("p1"));
		final List<JsonNode> mapped = List.of(rawNode("p1"));

		when(this.pollRepository.findByInitiatedGreaterThan(1000L)).thenReturn(polls);
		when(this.pollRawMapper.toRawJson(eq(polls))).thenReturn(mapped);

		final var result = this.pollQueryService.findInitiatedAfter(1000L);

		assertEquals(mapped, result);
		verify(this.pollRepository).findByInitiatedGreaterThan(1000L);
		verify(this.pollRawMapper).toRawJson(polls);
		verifyNoMoreInteractions(this.pollRepository, this.pollRawMapper);
	}

	private static Poll poll(final String id) {
		return Poll.builder().id(id).raw(raw(id)).build();
	}

	private static LinkedHashMap<String, Object> raw(final String id) {
		final LinkedHashMap<String, Object> raw = new LinkedHashMap<>();
		raw.put("id", id);
		return raw;
	}

	private static JsonNode rawNode(final String id) {
		return JsonNodeFactory.instance.objectNode().put("id", id);
	}
}
