package org.elypia.converters4deltaspike;

import org.apache.deltaspike.core.api.config.ConfigResolver;

import java.util.Objects;

/**
 * <p>
 *     Converts a character or numeric value representing a character
 *     into a Java {@link Character} object.
 * </p>
 *
 * <p>
 *     This converter may also accept hexadecimal {@link String}s if obtaining
 *     a character from it's numeric value is desired.
 *
 *     This could be useful in cases where there are concerns regarding putting
 *     the actual character in it's raw form, such as system/file encodings between
 *     clients, applications, and servers.
 * </p>
 *
 * @author seth@elypia.org (Seth Falco)
 * @since 1.0.0
 */
public class CharacterConverter implements ConfigResolver.Converter<Character> {

    /** This prefix allows us to determine an input is a hexadecimal {@link String}. */
    private static final String HEX_PREFIX = "0x";

    /**
     * @param value The value of the configuration property.
     * @return A {@link Character} which represents the configuration property value.
     * @throws NullPointerException If the value is null.
     * @throws IllegalArgumentException If an empty string is provided as the value.
     * @throws NumberFormatException If a hexadecimal {@link String} is provided, but
     * can not be parsed as an {@link Integer}.
     */
    @Override
    public Character convert(String value) {
        Objects.requireNonNull(value, "Value can't be null.");

        if (value.isEmpty())
            throw new IllegalArgumentException("Value can't be empty.");

        if (value.length() == 1)
            return value.charAt(0);

        if (value.toLowerCase().startsWith(HEX_PREFIX)) {
            int hexValue = Integer.parseInt(value, HEX_PREFIX.length(), value.length(), 16);
            return (char)hexValue;
        }

        throw new IllegalArgumentException("Value can't be represented as a character.");
    }
}
