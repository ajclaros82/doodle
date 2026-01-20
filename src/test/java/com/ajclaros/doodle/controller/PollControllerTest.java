package com.ajclaros.doodle.controller;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PollController.class)
class PollControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PollRepository pollRepository;

	@Test
	void byInitiator_returnsPolls() throws Exception {
		when(this.pollRepository.findByInitiatorNameContainingIgnoreCase(eq("john")))
				.thenReturn(List.of(Poll.builder().id("p1").build()));

		this.mockMvc.perform(get("/api/v1/polls/by-initiator").param("name", "john")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

		verify(this.pollRepository).findByInitiatorNameContainingIgnoreCase("john");
	}

	@Test
	void byTitle_returnsPolls() throws Exception {
		when(this.pollRepository.findByTitleContainingIgnoreCase(eq("meeting")))
				.thenReturn(List.of(Poll.builder().id("p1").build()));

		this.mockMvc.perform(get("/api/v1/polls/by-title").param("title", "meeting")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

		verify(this.pollRepository).findByTitleContainingIgnoreCase("meeting");
	}

	@Test
	void initiatedAfter_usesMsParam() throws Exception {
		when(this.pollRepository.findByInitiatedGreaterThanEqual(anyLong()))
				.thenReturn(List.of(Poll.builder().id("p1").build()));

		this.mockMvc.perform(get("/api/v1/polls/initiated-after").param("initiatedAfterMs", "1000"))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

		verify(this.pollRepository).findByInitiatedGreaterThanEqual(1001L);
	}

	@Test
	void initiatedAfter_requiresParam() throws Exception {
		this.mockMvc.perform(get("/api/v1/polls/initiated-after")).andExpect(status().isBadRequest());
	}
}
