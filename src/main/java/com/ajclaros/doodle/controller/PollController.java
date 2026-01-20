package com.ajclaros.doodle.controller;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	private final PollRepository pollRepository;

	/**
	 * 1) Get polls by initiator name (partial, case-insensitive).
	 * <p>
	 * Example: GET /api/v1/polls/by-initiator?name=john
	 */
	@GetMapping("/by-initiator")
	public List<Poll> getByInitiatorName(@RequestParam("name") String initiatorName) {
		return this.pollRepository.findByInitiatorNameContainingIgnoreCase(initiatorName);
	}

	/**
	 * 2) Get polls by title.
	 * <p>
	 * Example: GET /api/v1/polls/by-title?title=meeting
	 */
	@GetMapping("/by-title")
	public List<Poll> getByByTitle(@RequestParam("title") String title) {
		return this.pollRepository.findByTitleContainingIgnoreCase(title);
	}

	/**
	 * 3) List all polls created after a certain date.
	 * <p>
	 * The poll field `initiated` is a unix epoch timestamp in milliseconds.
	 * <p>
	 * Provide either: - initiatedAfterMs (epoch millis) - initiatedAfter (ISO-8601
	 * instant, e.g. 2026-01-01T00:00:00Z)
	 */
	@GetMapping("/initiated-after")
	public ResponseEntity<List<Poll>> getInitiatedAfter(
			@RequestParam(value = "initiatedAfterMs", required = false) Long initiatedAfterMs,
			@RequestParam(value = "initiatedAfter", required = false) String initiatedAfter) {

		final long threshold;
		if (initiatedAfterMs != null) {
			threshold = initiatedAfterMs;
		} else if (initiatedAfter != null && !initiatedAfter.isBlank()) {
			threshold = Instant.parse(initiatedAfter).toEpochMilli();
		} else {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(this.pollRepository.findByInitiatedGreaterThanEqual(threshold + 1));
	}
}
