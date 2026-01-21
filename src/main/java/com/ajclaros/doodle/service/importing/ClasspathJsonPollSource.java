package com.ajclaros.doodle.service.importing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Loads poll JSON from a classpath resource.
 */
@Component
@RequiredArgsConstructor
public class ClasspathJsonPollSource {

	private final ObjectMapper objectMapper;

	/**
	 * Loads and parses the sample_data.json file from the classpath.
	 *
	 * @return the root JSON node
	 * @throws IllegalStateException
	 *             if loading or parsing fails
	 */
	public JsonNode load() {
		final ClassPathResource resource = new ClassPathResource("sample_data.json");

		try (final InputStream is = resource.getInputStream()) {
			return this.objectMapper.readTree(is);
		} catch (final Exception e) {
			throw new IllegalStateException("Failed to load sample_data.json", e);
		}
	}
}
