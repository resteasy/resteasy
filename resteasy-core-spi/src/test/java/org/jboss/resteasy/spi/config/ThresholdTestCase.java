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

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ThresholdTestCase {

    /**
     * Tests parsing a threshold with {@link Threshold#valueOf(String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void parsing() throws Exception {
        test(Threshold.of(512L, SizeUnit.BYTE), Threshold.valueOf("512"));
        test(Threshold.of(1L, SizeUnit.MEGABYTE), Threshold.valueOf("1MB"));
        test(Threshold.of(1L, SizeUnit.MEGABYTE), Threshold.valueOf("1mb"));
        test(Threshold.of(1L, SizeUnit.MEGABYTE), Threshold.valueOf("1 MB"));
        test(Threshold.of(1L, SizeUnit.MEGABYTE), Threshold.valueOf("1 mb"));
        test(Threshold.NONE, Threshold.valueOf("-1 MB"));
        test(Threshold.DEFAULT, Threshold.valueOf(""));

        // Test each unit
        final Random r = new Random();
        for (SizeUnit unit : SizeUnit.values()) {
            final long size = createSize(r);
            test(Threshold.of(size, unit), Threshold.valueOf(size + unit.abbreviation()));
        }
    }

    private void test(final Threshold expected, final Threshold tested) {
        Assertions.assertEquals(expected, tested, String.format("Expected %s got %s", expected, tested));
    }

    private static long createSize(final Random r) {
        final int result = r.nextInt(512);
        return result < 1 ? 1L : result;
    }
}
