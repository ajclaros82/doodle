package com.ajclaros.doodle.service;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.ajclaros.doodle.service.importing.ClasspathJsonPollSource;
import com.ajclaros.doodle.service.importing.JsonPollParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PollImportServiceTest {

	private final ClasspathJsonPollSource pollSource = mock(ClasspathJsonPollSource.class);
	private final JsonPollParser pollParser = mock(JsonPollParser.class);
	private final PollRepository pollRepository = mock(PollRepository.class);

	private final PollImportService service = new PollImportService(this.pollSource, this.pollParser,
			this.pollRepository);

	@Test
	void importFromSampleJson_savesParsedPolls_andReturnsCount() {
		final JsonNode root = JsonNodeFactory.instance.arrayNode();
		when(this.pollSource.load()).thenReturn(root);

		final List<Poll> parsed = List.of(poll("p1"), poll("p2"));
		when(this.pollParser.parse(eq(root))).thenReturn(parsed);
		when(this.pollRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		final int imported = this.service.importFromSampleJson();

		assertEquals(2, imported);
		verify(this.pollSource).load();
		verify(this.pollParser).parse(root);
		verify(this.pollRepository).saveAll(parsed);
		verifyNoMoreInteractions(this.pollSource, this.pollParser, this.pollRepository);
	}

	@Test
	void importFromSampleJson_persistsRawAndInitiatorWhenProvided() {
		final JsonNode root = JsonNodeFactory.instance.arrayNode();
		when(this.pollSource.load()).thenReturn(root);

		final Poll p1 = poll("p1");
		when(this.pollParser.parse(eq(root))).thenReturn(List.of(p1));
		when(this.pollRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		this.service.importFromSampleJson();

		verify(this.pollRepository).saveAll(argThat(list -> {
			final List<Poll> polls = (List<Poll>) list;
			final Poll first = polls.getFirst();

			assertNotNull(first.getId());
			assertNotNull(first.getRaw());
			assertEquals(first.getId(), first.getRaw().get("id"));
			return true;
		}));
	}

	private static Poll poll(final String id) {
		final LinkedHashMap<String, Object> raw = new LinkedHashMap<>();
		raw.put("id", id);
		return Poll.builder().id(id).raw(raw).build();
	}
}
