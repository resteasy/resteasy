/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package org.jboss.resteasy.spi.config;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * Represents a threshold for a unit size.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Threshold {

    public static final Threshold NONE = new Threshold(-1L, SizeUnit.BYTE);
    public static final Threshold DEFAULT = new Threshold(512L, SizeUnit.KILOBYTE);
    private static final Pattern PATTERN = Pattern.compile("(?<size>-?(?!0)\\d+)\\s*(?<unit>(?:ZB|EB|TB|PB|GB|MB|KB|B)\\b)?");

    private final long size;
    private final SizeUnit unit;
    private final long bytes;

    /**
     * Creates a new threshold.
     *
     * @param size the maximum size of the threshold
     * @param unit the unit of measurement to calculate the size in bytes
     */
    private Threshold(final long size, final SizeUnit unit) {
        this.size = size;
        this.unit = unit == null ? SizeUnit.BYTE : unit;
        this.bytes = size > 0 ? this.unit.toBytes(size) : -1L;
    }

    /**
     * Creates a new threshold.
     * <p>
     * If the {@code size} is less than 0, then the {@link #NONE} instance is returned regardless of the {@code unit}.
     * </p>
     *
     * @param size the maximum size of the threshold
     * @param unit the unit of measurement to calculate the size in bytes
     */
    public static Threshold of(final long size, final SizeUnit unit) {
        if (size < 0) {
            return NONE;
        }
        return new Threshold(size, unit);
    }

    /**
     * The size in bytes for this threshold.
     *
     * @return the size in bytes
     */
    public long toBytes() {
        return bytes;
    }

    /**
     * Returns the size unit for this threshold.
     *
     * @return the size unit
     */
    public SizeUnit sizeUnit() {
        return unit;
    }

    /**
     * Checks if the given size is greater than the bytes available for the threshold.
     *
     * @param size the size to validate
     *
     * @return {@code false} if the size is greater than the bytes allowed for this threshold, otherwise {@code true}
     */
    public boolean reached(final long size) {
        if (bytes == -1) {
            return false;
        }
        return size > bytes;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(size) + Objects.hash(unit);
    }

    /**
     * Parses the given string representation of a threshold. For example a value of "1MB" would return a threshold
     * with a size of 1 megabyte.
     *
     * @param value the value to parse
     *
     * @return a threshold based on the parsed string
     */
    public static Threshold valueOf(final String value) {
        // The value should be something like 1 MB or 1MB
        final Matcher matcher = PATTERN
                .matcher(Objects.requireNonNull(value, Messages.MESSAGES.nullParameter("value")).toUpperCase(Locale.ROOT));
        if (!matcher.find()) {
            return DEFAULT;
        }
        final String stringSize = matcher.group("size");
        final String stringUnit = matcher.group("unit");
        final long size;
        if (stringSize == null || stringSize.isBlank()) {
            return DEFAULT;
        } else {
            size = Long.parseLong(stringSize);
        }
        if (size < 0L) {
            return NONE;
        }
        SizeUnit unit = null;
        for (SizeUnit u : SizeUnit.values()) {
            if (u.abbreviation().equals(stringUnit)) {
                unit = u;
                break;
            }
        }
        return of(size, unit == null ? SizeUnit.BYTE : unit);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Threshold)) {
            return false;
        }
        final Threshold other = (Threshold) obj;
        return size == other.size &&
                unit == other.unit;
    }

    @Override
    public String toString() {
        return size + unit.abbreviation();
    }
}
