package com.ajclaros.doodle.config;

import com.ajclaros.doodle.repository.PollRepository;
import com.ajclaros.doodle.service.PollImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to import polls on application startup.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PollImportOnStartup {

	private final PollRepository pollRepository;

	private final PollImportService pollImportService;

	/**
	 * Imports sample_data.json on startup only if the polls collection is empty.
	 */
	@Bean
	ApplicationRunner importPollsOnStartup() {
		return (ApplicationArguments args) -> {
			final long existing = this.pollRepository.count();
			if (existing > 0) {
				log.info("Polls import skipped: repository already has {} documents", existing);
				return;
			}

			final int imported = this.pollImportService.importFromSampleJson();
			log.info("Polls import completed: {} documents", imported);
		};
	}

}
