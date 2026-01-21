package com.ajclaros.doodle.service.importing;

import com.ajclaros.doodle.model.Poll;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.ajclaros.doodle.util.JsonUtils.*;

/**
 * Parses a JSON array of polls into {@link Poll} objects.
 */
@Component
@RequiredArgsConstructor
public class JsonPollParser {

	private static final String ROOT_SHAPE_ERROR = "Expected root payload to be a JSON array";

	private final ObjectMapper objectMapper;

	/**
	 * Parses the given JSON root node into a list of polls.
	 *
	 * @param root
	 *            the root JSON node
	 * @return the list of parsed polls
	 * @throws IllegalStateException
	 *             if the root node is not an array
	 */
	public List<Poll> parse(final JsonNode root) {
		if (root == null || !root.isArray()) {
			throw new IllegalStateException(ROOT_SHAPE_ERROR);
		}

		final List<Poll> polls = new ArrayList<>();
		for (final JsonNode pollNode : root) {
			final String id = extractIdOrNull(pollNode);
			if (id == null) {
				continue;
			}
			polls.add(this.toPoll(pollNode, id));
		}
		return polls;
	}

	private static String extractIdOrNull(final JsonNode pollNode) {
		return readNonBlankText(pollNode, "id");
	}

	private Poll toPoll(final JsonNode pollNode, final String id) {
		return Poll.builder().id(id).initiated(readLongOrNull(pollNode, "initiated"))
				.title(readTextOrNull(pollNode, "title")).initiator(parseInitiatorOrNull(pollNode.path("initiator")))
				.raw(this.toRawMap(pollNode, id)).build();
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> toRawMap(final JsonNode pollNode, final String id) {
		try {
			// We store the original payload as a nested MongoDB document.
			return this.objectMapper.convertValue(pollNode, LinkedHashMap.class);
		} catch (final IllegalArgumentException e) {
			throw new IllegalStateException("Failed to convert raw poll payload for id=" + id, e);
		}
	}

	private static Poll.Initiator parseInitiatorOrNull(final JsonNode initiatorNode) {
		if (initiatorNode == null || initiatorNode.isMissingNode() || initiatorNode.isNull()) {
			return null;
		}

		return Poll.Initiator.builder().name(readTextOrNull(initiatorNode, "name")).build();
	}
}
