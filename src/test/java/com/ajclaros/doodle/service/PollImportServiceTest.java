package com.ajclaros.doodle.service;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class PollImportServiceTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final PollRepository pollRepository = mock(PollRepository.class);

	private final PollImportService service = new PollImportService(this.objectMapper, this.pollRepository);

	@Test
	void importFromSampleJson_savesParsedPolls_andReturnsCount() {
		when(this.pollRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		final int imported = this.service.importFromSampleJson();

		// The test classpath provides its own sample_data.json (under
		// src/test/resources)
		// so this count is deterministic.
		assertEquals(2, imported);

		verify(this.pollRepository).saveAll(org.mockito.ArgumentMatchers.argThat(list -> {
			final List<Poll> polls = (List<Poll>) list;
			assertEquals(2, polls.size());
			assertEquals("p1", polls.get(0).getId());
			assertEquals("p2", polls.get(1).getId());
			return true;
		}));
	}

	@Test
	void importFromSampleJson_mapsInitiatorAndStoresRawPayload() {
		when(this.pollRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		this.service.importFromSampleJson();

		verify(this.pollRepository).saveAll(org.mockito.ArgumentMatchers.argThat(list -> {
			final List<Poll> polls = (List<Poll>) list;

			final Poll first = polls.getFirst();
			assertEquals("p1", first.getId());
			assertEquals(1000L, first.getInitiated());
			assertEquals("First", first.getTitle());
			assertNotNull(first.getInitiator());
			assertEquals("John", first.getInitiator().getName());
			assertEquals("john@example.com", first.getInitiator().getEmail());
			assertEquals(Boolean.TRUE, first.getInitiator().getNotify());

			// raw payload should keep additional fields (e.g., adminKey) while excluding
			// duplicated typed fields
			assertNotNull(first.getRaw());

			final Poll second = polls.get(1);
			assertEquals("p2", second.getId());
			assertNotNull(second.getRaw());
			// initiator missing in JSON -> null
			assertNull(second.getInitiator());
			return true;
		}));
	}
}
