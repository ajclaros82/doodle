package com.ajclaros.doodle.service;

import com.ajclaros.doodle.mapper.PollRawMapper;
import com.ajclaros.doodle.repository.PollRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Query service for polls. Keeps repository access out of controllers and
 * centralizes read/query logic.
 */
@Service
@RequiredArgsConstructor
public class PollQueryService {

	private final PollRepository pollRepository;
	private final PollRawMapper pollRawMapper;

	/**
	 * Finds polls where the initiator name contains the given string (case
	 * insensitive).
	 * <p>
	 *
	 * @param initiatorName
	 *            the initiator name substring to search for
	 * @return the list of matching polls as raw JSON nodes
	 */
	public List<JsonNode> findByInitiatorName(final String initiatorName) {
		return this.pollRawMapper.toRawJson(this.pollRepository.findByInitiatorNameContainingIgnoreCase(initiatorName));
	}

	/**
	 * Finds polls where the title contains the given string (case insensitive).
	 * <p>
	 *
	 * @param title
	 *            the title substring to search for
	 * @return the list of matching polls as raw JSON nodes
	 */
	public List<JsonNode> findByTitle(final String title) {
		return this.pollRawMapper.toRawJson(this.pollRepository.findByTitleContainingIgnoreCase(title));
	}

	/**
	 * Finds polls initiated after the given timestamp.
	 * <p>
	 *
	 * @param initiatedAfterMs
	 *            the timestamp in milliseconds
	 * @return the list of matching polls as raw JSON nodes
	 */
	public List<JsonNode> findInitiatedAfter(final long initiatedAfterMs) {
		return this.pollRawMapper.toRawJson(this.pollRepository.findByInitiatedGreaterThan(initiatedAfterMs));
	}
}
