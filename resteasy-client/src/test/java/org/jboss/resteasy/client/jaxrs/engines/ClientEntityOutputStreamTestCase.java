/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.client.jaxrs.engines;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.entity.AbstractHttpEntity;
import org.jboss.resteasy.spi.config.SizeUnit;
import org.jboss.resteasy.spi.config.Threshold;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ClientEntityOutputStream}, specifically verifying that
 * {@link ClientEntityOutputStream#toEntity()} works correctly when the entity
 * body exceeds the in-memory threshold and spills to a temporary file.
 *
 * @see <a href="https://issues.redhat.com/browse/RESTEASY-3691">RESTEASY-3691</a>
 */
public class ClientEntityOutputStreamTestCase {

    /**
     * Tests that {@link ClientEntityOutputStream#toEntity()} returns a valid
     * entity whose content can be read via {@code getContent()} when the data
     * exceeds the in-memory threshold and is written to a file.
     * <p>
     * Before the RESTEASY-3691 fix, this test fails with an
     * {@link IllegalAccessError} because the private inner class
     * {@code PathHttpEntity} attempted to instantiate a {@code protected}
     * inner class ({@code EntityInputStream}) from a different package.
     * </p>
     */
    @Test
    public void toEntityWithFileSpillGetContent() throws Exception {
        final Threshold threshold = Threshold.of(10, SizeUnit.BYTE);
        try (ClientEntityOutputStream out = new ClientEntityOutputStream(threshold, null, () -> "test-entity")) {
            final int len = 20;
            final byte[] data = new byte[len];
            Arrays.fill(data, (byte) 'x');
            out.write(data);
            out.close();

            final AbstractHttpEntity entity = out.toEntity();

            try (InputStream in = entity.getContent()) {
                final byte[] result = in.readAllBytes();
                Assertions.assertEquals(len, result.length,
                        "Read data length should match written data length");
                for (int i = 0; i < result.length; i++) {
                    Assertions.assertEquals('x', (char) result[i],
                            String.format("Byte at position %d should be 'x'", i));
                }
            }
        }
    }

    /**
     * Tests that {@link ClientEntityOutputStream#toEntity()} returns a valid
     * entity whose content can be written via {@code writeTo()} when the data
     * exceeds the in-memory threshold.
     */
    @Test
    public void toEntityWithFileSpillWriteTo() throws Exception {
        final Threshold threshold = Threshold.of(10, SizeUnit.BYTE);
        try (ClientEntityOutputStream out = new ClientEntityOutputStream(threshold, null, () -> "test-entity")) {
            final int len = 20;
            final byte[] data = new byte[len];
            Arrays.fill(data, (byte) 'y');
            out.write(data);
            out.close();

            final AbstractHttpEntity entity = out.toEntity();

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            entity.writeTo(baos);
            final byte[] result = baos.toByteArray();
            Assertions.assertEquals(len, result.length,
                    "Written data length should match original data length");
            for (int i = 0; i < result.length; i++) {
                Assertions.assertEquals('y', (char) result[i],
                        String.format("Byte at position %d should be 'y'", i));
            }
        }
    }

    /**
     * Tests that {@link ClientEntityOutputStream#toEntity()} returns a valid
     * in-memory entity when the data does NOT exceed the threshold.
     */
    @Test
    public void toEntityInMemory() throws Exception {
        final Threshold threshold = Threshold.of(100, SizeUnit.BYTE);
        try (ClientEntityOutputStream out = new ClientEntityOutputStream(threshold, null, () -> "test-entity")) {
            final int len = 20;
            final byte[] data = new byte[len];
            Arrays.fill(data, (byte) 'z');
            out.write(data);
            out.close();

            final AbstractHttpEntity entity = out.toEntity();

            try (InputStream in = entity.getContent()) {
                final byte[] result = in.readAllBytes();
                Assertions.assertEquals(len, result.length);
                for (int i = 0; i < result.length; i++) {
                    Assertions.assertEquals('z', (char) result[i]);
                }
            }
        }
    }
}
