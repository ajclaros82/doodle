package com.ajclaros.doodle.controller;

import com.ajclaros.doodle.service.PollQueryService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
	private PollQueryService pollQueryService;

	@Test
	void byInitiator_returnsServiceOutput() throws Exception {
		when(this.pollQueryService.findByInitiatorName(eq("john")))
				.thenReturn(List.of(JsonNodeFactory.instance.objectNode().put("id", "p1")));

		this.mockMvc.perform(get("/api/v1/polls/by-initiator").param("name", "john")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].id").value("p1"));

		verify(this.pollQueryService).findByInitiatorName("john");
	}

	@Test
	void byTitle_returnsServiceOutput() throws Exception {
		when(this.pollQueryService.findByTitle(eq("meeting")))
				.thenReturn(List.of(JsonNodeFactory.instance.objectNode().put("id", "p1")));

		this.mockMvc.perform(get("/api/v1/polls/by-title").param("title", "meeting")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].id").value("p1"));

		verify(this.pollQueryService).findByTitle("meeting");
	}

	@Test
	void initiatedAfter_returnsServiceOutput() throws Exception {
		when(this.pollQueryService.findInitiatedAfter(anyLong()))
				.thenReturn(List.of(JsonNodeFactory.instance.objectNode().put("id", "p1")));

		this.mockMvc.perform(get("/api/v1/polls/initiated-after").param("initiatedAfter", "1970-01-01T00:00:01Z"))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].id").value("p1"));

		verify(this.pollQueryService).findInitiatedAfter(1000L);
	}

	@Test
	void initiatedAfter_requiresParam() throws Exception {
		this.mockMvc.perform(get("/api/v1/polls/initiated-after")).andExpect(status().isBadRequest());

		verify(this.pollQueryService, never()).findInitiatedAfter(anyLong());
	}
}
