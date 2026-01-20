package com.ajclaros.doodle.util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Utility methods for reading values from JsonNode objects.
 */
public class JsonUtils {

	/**
	 * Reads the text value of the specified field from the given JsonNode.
	 *
	 * @param node
	 *            the JsonNode to read from
	 * @param field
	 *            the field name
	 * @return the text value, or null if the field is missing or null
	 */
	public static String readTextOrNull(final JsonNode node, final String field) {
		final JsonNode valueNode = node.get(field);
		if (valueNode == null || valueNode.isNull()) {
			return null;
		}
		return valueNode.asText(null);
	}

	/**
	 * Reads the text value of the specified field from the given JsonNode,
	 * returning null if the value is blank.
	 *
	 * @param node
	 *            the JsonNode to read from
	 * @param field
	 *            the field name
	 * @return the non-blank text value, or null if the field is missing, null, or
	 *         blank
	 */
	public static String readNonBlankText(final JsonNode node, final String field) {
		final String value = readTextOrNull(node, field);
		return (value == null || value.isBlank()) ? null : value;
	}

	/**
	 * Reads the long value of the specified field from the given JsonNode.
	 *
	 * @param node
	 *            the JsonNode to read from
	 * @param field
	 *            the field name
	 * @return the long value, or null if the field is missing or null
	 */
	public static Long readLongOrNull(final JsonNode node, final String field) {
		final JsonNode valueNode = node.get(field);
		return (valueNode == null || valueNode.isNull()) ? null : valueNode.asLong();
	}

	/**
	 * Reads the boolean value of the specified field from the given JsonNode.
	 *
	 * @param node
	 *            the JsonNode to read from
	 * @param field
	 *            the field name
	 * @return the boolean value, or null if the field is missing or null
	 */
	public static Boolean readBooleanOrNull(final JsonNode node, final String field) {
		final JsonNode valueNode = node.get(field);
		return (valueNode == null || valueNode.isNull()) ? null : valueNode.asBoolean();
	}

}
