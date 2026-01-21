package com.ajclaros.doodle.repository;

import com.ajclaros.doodle.model.Poll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class PollMongoRepositoryTest {

	@Container
	static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

	@DynamicPropertySource
	static void mongoProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
	}

	@Autowired
	private PollRepository pollRepository;

	@Test
	void queriesReturnFullPollDocuments() {
		this.pollRepository.save(poll("p1", 1000L, "My first poll", "John Doe"));
		this.pollRepository.save(poll("p2", 2000L, "Another poll", "Jane Doe"));

		final Poll initiated = single(this.pollRepository.findByInitiatedGreaterThan(1500L));
		assertFullPoll(initiated, "p2", 2000L, "Another poll", "Jane Doe");

		final Poll byTitle = single(this.pollRepository.findByTitleContainingIgnoreCase("first"));
		assertFullPoll(byTitle, "p1", 1000L, "My first poll", "John Doe");

		final Poll byInitiator = single(this.pollRepository.findByInitiatorNameContainingIgnoreCase("john"));
		assertFullPoll(byInitiator, "p1", 1000L, "My first poll", "John Doe");
	}

	private static Poll poll(String id, Long initiated, String title, String initiatorName) {
		return Poll.builder().id(id).initiated(initiated).title(title)
				.initiator(Poll.Initiator.builder().name(initiatorName).build()).raw("{\"id\":\"" + id + "\"}").build();
	}

	private static Poll single(List<Poll> polls) {
		assertNotNull(polls);
		assertEquals(1, polls.size(), "Expected exactly one poll");
		return polls.getFirst();
	}

	private static void assertFullPoll(Poll poll, String expectedId, Long expectedInitiated, String expectedTitle,
			String expectedInitiatorName) {
		assertNotNull(poll);
		assertEquals(expectedId, poll.getId());
		assertEquals(expectedInitiated, poll.getInitiated());
		assertEquals(expectedTitle, poll.getTitle());

		assertNotNull(poll.getInitiator());
		assertEquals(expectedInitiatorName, poll.getInitiator().getName());

		assertNotNull(poll.getRaw());
		assertFalse(poll.getRaw().isBlank());
	}
}
