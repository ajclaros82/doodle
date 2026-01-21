package com.ajclaros.doodle.service;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.ajclaros.doodle.service.importing.ClasspathJsonPollSource;
import com.ajclaros.doodle.service.importing.JsonPollParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to import polls from sample_data.json into MongoDB.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PollImportService {

	private final ClasspathJsonPollSource pollSource;
	private final JsonPollParser pollParser;
	private final PollRepository pollRepository;

	/**
	 * Imports polls from sample_data.json into the MongoDB collection.
	 *
	 * @return the number of imported polls
	 */
	public int importFromSampleJson() {
		final List<Poll> polls = this.pollParser.parse(this.pollSource.load());
		this.pollRepository.saveAll(polls);

		log.info("Imported {} polls", polls.size());
		return polls.size();
	}
}
