package com.ajclaros.doodle.repository;

import com.ajclaros.doodle.model.Poll;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for Poll entities.
 */
public interface PollRepository extends MongoRepository<Poll, String> {

	List<Poll> findByInitiatedGreaterThan(Long initiated);

	List<Poll> findByTitleContainingIgnoreCase(String title);

	List<Poll> findByInitiatorNameContainingIgnoreCase(String initiatorName);
}
