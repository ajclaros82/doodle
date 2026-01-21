package com.ajclaros.doodle.controller;

import com.ajclaros.doodle.mapper.PollRawMapper;
import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PollController.class)
class PollControllerTest {

	@TestConfiguration
	static class JacksonTestConfig {
		@Bean
		ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PollRepository pollRepository;

	@MockitoBean
	private PollRawMapper pollRawMapper;

	@Test
	void byInitiator_returnsMapperOutput() throws Exception {
		final List<Poll> polls = List.of(poll("p1", 1L, "t", "John"));
		when(this.pollRepository.findByInitiatorNameContainingIgnoreCase(eq("john"))).thenReturn(polls);
		when(this.pollRawMapper.toRawJson(eq(polls)))
				.thenReturn(List.of(JsonNodeFactory.instance.objectNode().put("id", "p1")));

		this.mockMvc.perform(get("/api/v1/polls/by-initiator").param("name", "john")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].id").value("p1"));

		verify(this.pollRepository).findByInitiatorNameContainingIgnoreCase("john");
		verify(this.pollRawMapper).toRawJson(polls);
	}

	@Test
	void byTitle_returnsMapperOutput() throws Exception {
		final List<Poll> polls = List.of(poll("p1", 2L, "meeting", null));
		when(this.pollRepository.findByTitleContainingIgnoreCase(eq("meeting"))).thenReturn(polls);
		when(this.pollRawMapper.toRawJson(eq(polls)))
				.thenReturn(List.of(JsonNodeFactory.instance.objectNode().put("id", "p1")));

		this.mockMvc.perform(get("/api/v1/polls/by-title").param("title", "meeting")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].id").value("p1"));

		verify(this.pollRepository).findByTitleContainingIgnoreCase("meeting");
		verify(this.pollRawMapper).toRawJson(polls);
	}

	@Test
	void initiatedAfter_returnsMapperOutput() throws Exception {
		final List<Poll> polls = List.of(poll("p1", 1001L, "t", null));
		when(this.pollRepository.findByInitiatedGreaterThan(anyLong())).thenReturn(polls);
		when(this.pollRawMapper.toRawJson(eq(polls)))
				.thenReturn(List.of(JsonNodeFactory.instance.objectNode().put("id", "p1")));

		this.mockMvc.perform(get("/api/v1/polls/initiated-after").param("initiatedAfter", "1970-01-01T00:00:01Z"))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].id").value("p1"));

		verify(this.pollRepository).findByInitiatedGreaterThan(1000L);
		verify(this.pollRawMapper).toRawJson(polls);
	}

	@Test
	void initiatedAfter_requiresParam() throws Exception {
		this.mockMvc.perform(get("/api/v1/polls/initiated-after")).andExpect(status().isBadRequest());

		verify(this.pollRepository, org.mockito.Mockito.never()).findByInitiatedGreaterThan(anyLong());
		verify(this.pollRawMapper, org.mockito.Mockito.never()).toRawJson(anyList());
	}

	private static Poll poll(final String id, final Long initiated, final String title, final String initiatorName) {
		final Poll.Initiator initiator = initiatorName == null
				? null
				: Poll.Initiator.builder().name(initiatorName).build();

		return Poll.builder().id(id).initiated(initiated).title(title).initiator(initiator)
				.raw("{\"id\":\"" + id + "\"}").build();
	}
}
