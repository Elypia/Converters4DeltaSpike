package org.elypia.converters4deltaspike;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author seth@elypia.org (Seth Falco)
 * @since 1.0.0
 */
public class DurationConverterTest {

    @Test
    public void testConverteringDefault() {
        DurationConverter converter = new DurationConverter();

        final Duration expected = Duration.ofSeconds(1);
        final Duration actual = converter.convert("1000");

        assertEquals(expected, actual);
    }

    @Test
    public void testConverteringDurationString() {
        DurationConverter converter = new DurationConverter();

        final Duration expected = Duration.ofMinutes(3064);
        final Duration actual = converter.convert("P2DT3H4M");

        assertEquals(expected, actual);
    }

    @Test
    public void testConverteringCustomTemporal() {
        DurationConverter converter = new DurationConverter(ChronoUnit.SECONDS);

        final Duration expected = Duration.ofSeconds(4);
        final Duration actual = converter.convert("4");

        assertEquals(expected, actual);
    }

    /**
     * As this is meant to be for technical usage, such as developers, or administrators
     * of software, we don't allow localized {@link Number}s such as <code>1,000</code>. Only programatically
     * correct values like <code>1000</code>.
     *
     * @param value Test value.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1,000", "Hello, world!", "100.000.000", "Z"})
    public void testConverteringInvalidNumbers(final String value) {
        DurationConverter converter = new DurationConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convert(value));
    }
}
