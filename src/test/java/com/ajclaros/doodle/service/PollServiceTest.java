package com.ajclaros.doodle.service;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class PollServiceTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final PollRepository pollRepository = mock(PollRepository.class);

	private final PollService service = new PollService(this.objectMapper, this.pollRepository);

	@Test
	void importFromSampleJson_savesParsedPolls_andReturnsCount() {
		when(this.pollRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		final int imported = this.service.importFromSampleJson();

		// test resources sample_data.json has 3 entries, one without id -> should be
		// skipped
		assertEquals(2, imported);

		verify(this.pollRepository).saveAll(argThat(list -> ((List<?>) list).size() == 2));
	}

	@Test
	void importFromSampleJson_mapsInitiatorName_andStoresRawJsonNode() {
		when(this.pollRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		this.service.importFromSampleJson();

		verify(this.pollRepository).saveAll(argThat(list -> {
			final List<Poll> polls = (List<Poll>) list;
			final Poll first = polls.getFirst();

			assertNotNull(first.getId());
			assertNotNull(first.getRaw());
			assertFalse(first.getRaw().isBlank());

			try {
				final var rawNode = this.objectMapper.readTree(first.getRaw());
				assertEquals(first.getId(), rawNode.path("id").asText());
			} catch (final Exception e) {
				fail("raw should be valid JSON", e);
			}

			if (first.getInitiator() != null) {
				assertNotNull(first.getInitiator().getName());
			}
			return true;
		}));
	}
}
