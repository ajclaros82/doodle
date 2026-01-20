package com.ajclaros.doodle.service;

import com.ajclaros.doodle.model.Poll;
import com.ajclaros.doodle.repository.PollRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ajclaros.doodle.util.JsonUtils.*;

/**
 * Service to import polls from sample_data.json into MongoDB.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PollImportService {

	private static final String SAMPLE_DATA_PATH = "sample_data.json";

	private final ObjectMapper objectMapper;

	private final PollRepository pollRepository;

	/**
	 * Imports polls from sample_data.json into the MongoDB collection.
	 *
	 * @return the number of imported polls
	 */
	public int importFromSampleJson() {
		final List<Poll> polls = this.readPollsFromClasspath();
		this.pollRepository.saveAll(polls);

		log.info("Imported {} polls", polls.size());
		return polls.size();
	}

	private List<Poll> readPollsFromClasspath() {
		final ClassPathResource resource = new ClassPathResource(SAMPLE_DATA_PATH);

		try (final InputStream is = resource.getInputStream()) {
			final JsonNode root = this.objectMapper.readTree(is);
			requireJsonArray(root);
			return this.parsePolls(root);
		} catch (final Exception e) {
			throw new IllegalStateException("Failed to import " + SAMPLE_DATA_PATH, e);
		}
	}

	private static void requireJsonArray(final JsonNode node) {
		if (node == null || !node.isArray()) {
			throw new IllegalStateException(SAMPLE_DATA_PATH + " must be a JSON array");
		}
	}

	private List<Poll> parsePolls(final JsonNode pollsArray) {
		final List<Poll> polls = new ArrayList<>();

		for (final JsonNode pollNode : pollsArray) {
			final String id = readNonBlankText(pollNode, "id");
			if (id == null) {
				continue;
			}

			polls.add(this.parsePoll(pollNode, id));
		}

		return polls;
	}

	@SneakyThrows
	private Poll parsePoll(final JsonNode pollNode, final String id) {
		final Long initiated = readLongOrNull(pollNode, "initiated");
		final String title = readTextOrNull(pollNode, "title");
		final Poll.Initiator initiator = parseInitiatorOrNull(pollNode.path("initiator"));
		final String raw = this.objectMapper.writeValueAsString(pollNode);

		return Poll.builder().id(id).initiated(initiated).title(title).initiator(initiator).raw(raw).build();
	}

	private static Poll.Initiator parseInitiatorOrNull(final JsonNode initiatorNode) {
		if (initiatorNode.isMissingNode() || initiatorNode.isNull()) {
			return null;
		}

		return Poll.Initiator.builder().name(readTextOrNull(initiatorNode, "name"))
				.email(readTextOrNull(initiatorNode, "email")).notify(readBooleanOrNull(initiatorNode, "notify"))
				.build();
	}

}
