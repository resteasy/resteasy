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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class SizeUnitTestCase {

    /**
     * Tests converting sizes into a human-readable form.
     */
    @Test
    public void humanReadable() {
        Assert.assertEquals("1KB", SizeUnit.toHumanReadable(1024L));
        Assert.assertEquals("10MB", SizeUnit.toHumanReadable(SizeUnit.MEGABYTE.toBytes(10L)));
        Assert.assertEquals("1.5KB", SizeUnit.toHumanReadable((512 * 3)));
        Assert.assertEquals("175.09MB", SizeUnit.toHumanReadable(183598209L));
    }

    /**
     * Tests converting the one unit to another.
     * <p>
     * Loops through each unit with a random number and attempts to convert the parent size into the units size.
     * </p>
     */
    @Test
    public void convert() {
        final Random r = new Random();
        for (SizeUnit unit : SizeUnit.values()) {
            final SizeUnit parent = unit.parent();
            // BYTE does not have a parent, so we will test that differently
            if (parent == null) {
                Assert.assertEquals(1024, SizeUnit.KILOBYTE.convert(1L, unit), 0);
                continue;
            }
            final long size = createSize(r);
            Assert.assertEquals(String.format("Failed to convert %d from %s to %s", size, parent, unit),
                    size, parent.convert((size * 1024L), unit), 0);
        }
    }

    private static long createSize(final Random r) {
        final int result = r.nextInt(128);
        return result < 1 ? 1L : result;
    }
}
