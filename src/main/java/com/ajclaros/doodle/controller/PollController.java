package com.ajclaros.doodle.controller;

import com.ajclaros.doodle.service.PollQueryService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for Poll-related endpoints.
 */
@RestController
@RequestMapping(path = "/api/v1/polls", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PollController {

	private final PollQueryService pollQueryService;

	/**
	 * 1) Get polls by initiator name (partial, case-insensitive).
	 * <p>
	 * Example: GET /api/v1/polls/by-initiator?name=john
	 */
	@GetMapping("/by-initiator")
	public List<JsonNode> getByInitiatorName(@RequestParam("name") String initiatorName) {
		return this.pollQueryService.findByInitiatorName(initiatorName);
	}

	/**
	 * 2) Get polls by title.
	 * <p>
	 * Example: GET /api/v1/polls/by-title?title=meeting
	 */
	@GetMapping("/by-title")
	public List<JsonNode> getByTitle(@RequestParam("title") String title) {
		return this.pollQueryService.findByTitle(title);
	}

	/**
	 * 3) List all polls created after a certain date.
	 * <p>
	 * The poll field `initiated` is a unix epoch timestamp in milliseconds.
	 * <p>
	 * Provide: initiatedAfter (ISO-8601 instant, e.g. 2026-01-01T00:00:00Z)
	 */
	@GetMapping("/initiated-after")
	public List<JsonNode> getInitiatedAfter(@RequestParam(value = "initiatedAfter") String initiatedAfter) {
		final long initiatedAfterMs = Instant.parse(initiatedAfter).toEpochMilli();
		return this.pollQueryService.findInitiatedAfter(initiatedAfterMs);
	}
}
