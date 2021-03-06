package org.elypia.converters4deltaspike;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author seth@elypia.org (Seth Falco)
 * @since 1.0.0
 */
public class PatternConverterTest {

    @Test
    public void testConverteringPattern() {
        PatternConverter converter = new PatternConverter();

        final String expected = "(?i)Ow.+O";
        final String actual = converter.convert("(?i)Ow.+O").toString();

        assertEquals(expected, actual);
    }
}
