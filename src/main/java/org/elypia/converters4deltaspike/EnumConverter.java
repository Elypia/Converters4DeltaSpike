/*
 * Copyright 2020-2020 Elypia CIC and Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elypia.converters4deltaspike;

import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *     Converts an {@link Enum} {@link String} into a Java {@link Enum} object.
 *     This converter may depend on Java naming conventions being followed for {@link Package}s,
 *     {@link Class}s, and {@link Enum} constants in future and is case-sensitive.
 *
 *     If you aren't following Java naming/coding conventions, then it's strongly recommend
 *     you adjust your project to do so to maximize reliability.
 * </p>
 *
 * This accepts values such as:
 * <ul>
 *     <li>
 *         <code>java.util.concurrent.TimeUnit.NANOSECONDS</code>
 *     </li>
 *     <li>
 *         <code>java.time.DayOfWeek#MONDAY</code>
 *     </li>
 * </ul>
 *
 * @author seth@elypia.org (Seth Falco)
 * @since 1.0.0
 */
public class EnumConverter implements ConfigResolver.Converter<Enum> {

    private static final Logger logger = LoggerFactory.getLogger(EnumConverter.class);

    /** Validates that the value is an enum, and splits it into it's components. */
    private static final Pattern ENUM_PATTERN = Pattern.compile("(?<package>(?:[a-z\\d.]+)*)\\.(?<class>[A-Za-z\\d]+)[#.](?<name>[A-Z\\d_]+)");

    /** The enum type required by the converter. */
    private final Class<? extends Enum> requiredEnumType;

    /**
     * Initialize the {@link EnumConverter} to accept any {@link Enum} constant.
     */
    public EnumConverter() {
        this(Enum.class);
    }

    /**
     * <p>
     *     Initialize the {@link EnumConverter} to accept enum constants for the specific enum type.
     * </p>
     *
     * It's possible to use this by extending this class and calling the super constructor, for example:
     *
     * <pre><code>
     * public class TimeUnitConverter extends EnumConverter {
     *
     *     public TimeUnitConverter() {
     *         super(TimeUnit.class);
     *     }
     * }
     * </code></pre>
     *
     * <p>
     *     This allows the use of the non-qualified {@link Enum} constant name instead,
     *     and provides validation of the type of enum loaded, so an {@link IllegalArgumentException}
     *     can be thrown instead of an {@link ClassCastException}.
     * </p>
     *
     * @param requiredEnumType An enum class, such as {@link TimeUnit}, or {@link DayOfWeek}.
     */
    public EnumConverter(Class<? extends Enum> requiredEnumType) {
        this.requiredEnumType = Objects.requireNonNull(requiredEnumType);
    }

    /**
     * @param value The value of the configuration property.
     * @return A {@link Enum} constant which represents the configuration property value.
     * @throws NullPointerException If the value is null.
     * @throws IllegalArgumentException If the value can not be mapped to an {@link Enum} constant.
     */
    @Override
    public Enum convert(String value) {
        Objects.requireNonNull(value, "Value can't be null.");

        if (requiredEnumType != null) {
            try {
                return Enum.valueOf(requiredEnumType, value);
            } catch (IllegalArgumentException ex) {
                logger.debug("EnumConverter with required type specified didn't match the constant by name, falling back to fully qualified name.");
            }
        }

        Matcher matcher = ENUM_PATTERN.matcher(value);

        if (!matcher.matches())
            throw new IllegalArgumentException("Value does not follow Java naming conventions, expecting input like: java.time.DayOfWeek.MONDAY");

        try {
            String className = matcher.group(1) + "." + matcher.group(2);

            Class type = Class.forName(className);

            if (!type.isEnum())
                throw new IllegalArgumentException("Value provided isn't an enumerated type.");

            if (!requiredEnumType.isAssignableFrom(type))
                throw new IllegalArgumentException("Class provided is not the required type.");

            return Enum.valueOf(type, matcher.group(3));
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Class specified doesn't exist.", ex);
        }
    }
}
