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

import java.text.DecimalFormat;

/**
 * Represents units of size for things such as memory or files.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public enum SizeUnit {
    BYTE(null, "B") {
        @Override
        public String toString(final long size) {
            return size + "B";
        }
    },
    KILOBYTE(BYTE, "KB"),
    MEGABYTE(KILOBYTE, "MB"),
    GIGABYTE(MEGABYTE, "GB"),
    TERABYTE(GIGABYTE, "TB"),
    PETABYTE(TERABYTE, "PB"),
    EXABYTE(TERABYTE, "EB"),

    ;

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private final SizeUnit parent;
    private final long sizeInBytes;
    private final String abbreviation;

    SizeUnit(final SizeUnit parent, final String abbreviation) {
        this.parent = parent;
        this.sizeInBytes = parent == null ? 1L : parent.sizeInBytes << 10;
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the abbreviation for the unit.
     *
     * @return the abbreviation for the unit
     */
    public String abbreviation() {
        return abbreviation;
    }

    /**
     * Converts the given size to bytes from this unit. For example {@code SizeUnit.KILOBYTES.toBytes(1L)} would return
     * 1024.
     *
     * @param size the size to convert
     *
     * @return the size in bytes
     */
    public long toBytes(final long size) {
        return Math.multiplyExact(sizeInBytes, size);
    }

    /**
     * Converts the given size to the given unit to this unit.
     *
     * @param size the size to convert
     * @param unit the unit to convert the size to
     *
     * @return the converted units
     */
    public double convert(final long size, final SizeUnit unit) {
        if (unit == BYTE) {
            return toBytes(size);
        }
        final long bytes = toBytes(size);
        return ((double) bytes / unit.sizeInBytes);
    }

    /**
     * Converts the size to a human-readable string format.
     * <p>
     * For example {@code SizeUnit.KILOBYTE.toString(1024L)} would return "1 KB".
     * </p>
     *
     * @param size the size, in bytes
     *
     * @return a human-readable size
     */
    public String toString(final long size) {
        return FORMAT.format((double) size / sizeInBytes) + abbreviation;
    }

    /**
     * Returns the parent, previous, unit for this unit.
     *
     * @return the parent unit or {@code null} in the case of {@link #BYTE}
     */
    public SizeUnit parent() {
        return parent;
    }

    /**
     * Converts the size, in bytes, to a human-readable form. For example {@code 1024} bytes return "1 KB".
     *
     * @param size the size, in bytes, to convert
     *
     * @return a human-readable size
     */
    public static String toHumanReadable(final long size) {
        if (size == 0L) {
            return "0B";
        }
        final SizeUnit[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            final SizeUnit unit = values[i];
            if (size >= unit.sizeInBytes) {
                return unit.toString(size);
            }
        }
        return size + "B";
    }
}
