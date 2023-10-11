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

package org.jboss.resteasy.spi;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.spi.config.SizeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class EntityOutputStreamTestCase {

    private Properties currentProperties;

    @BeforeEach
    public void captureProperties() {
        currentProperties = System.getProperties();
        System.setProperties(new Properties(currentProperties));
    }

    @AfterEach
    public void revertProperties() {
        System.setProperties(currentProperties);
    }

    /**
     * Test writing under the default size to memory and ensure no file is written to.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeToMemoryDefaults() throws Exception {
        try (
                EntityOutputStream out = new EntityOutputStream();
                BufferedOutputStream buffered = new BufferedOutputStream(out);) {
            final int len = (int) SizeUnit.MEGABYTE.toBytes(4L);
            for (int i = 0; i < len; i++) {
                buffered.write('a');
            }
            buffered.flush();
            // We should have been kept in memory
            final Path file = out.getFile();
            Assertions.assertNull(file, () -> "Expected data to be kept in memory.");
            Assertions.assertEquals(len, out.getContentLength(), () -> "Memory size differs from from the output size.");
            // Just consume the InputStream in order to delete the file
            try (InputStream in = out.toInputStream()) {
                Assertions.assertNotNull(in);
            }
        }
    }

    /**
     * Test writing to memory and test the contents of what was written.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeToMemory() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            // We should have been kept in memory
            final Path file = out.getFile();
            Assertions.assertNull(file, "Expected data to be kept in memory.");
            Assertions.assertEquals(len, out.getContentLength(), "Memory size differs from from the output size.");
            // Check the input stream, should contain all "a"'s
            try (InputStream in = out.toInputStream()) {
                // The in-memory data should be cleared now
                Assertions.assertEquals(0, out.getContentLength(), "Memory should have be cleared");
                Assertions.assertEquals(0, out.getAndClearMemory().length, "Memory should have be cleared");
                int b;
                int i = 0;
                while ((b = in.read()) != -1) {
                    Assertions.assertEquals('a', (char) b, String.format("Byte at %d was not 'a', but '%s'.", i, b));
                    i++;
                }
                Assertions.assertEquals(len, i, String.format("Expected %d bytes to be read, but %d were read.", len, i));
            }
        }
    }

    /**
     * Tests writing, with defaults, over the maximum out of in-memory bytes which should end up writing to a file.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeToFileDefaults() throws Exception {
        try (
                EntityOutputStream out = new EntityOutputStream();
                BufferedOutputStream buffered = new BufferedOutputStream(out);) {
            final int len = (int) SizeUnit.MEGABYTE.toBytes(10L);
            for (int i = 0; i < len; i++) {
                buffered.write('a');
            }
            buffered.flush();
            // We should have been written to a file
            final Path file = out.getFile();
            Assertions.assertNotNull(file, "Expected data to be written to a file.");
            Assertions.assertEquals(0, out.getAndClearMemory().length, "Expected the memory to be cleared");
            Assertions.assertEquals(Files.size(file), out.getContentLength(), "File size differs from from the output size.");
            // Just consume the InputStream in order to delete the file
            try (InputStream in = out.toInputStream()) {
                Assertions.assertNotNull(in);
            }
        }
    }

    /**
     * Tests the memory threshold is hit and the data is written to a file. The data itself is tested to ensure it
     * matches the expected content.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeToFile() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "10B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            // We should have been written to a file
            final Path file = out.getFile();
            Assertions.assertNotNull(file, "Expected data to be written to a file.");
            Assertions.assertEquals(0, out.getAndClearMemory().length, "Expected the memory to be cleared");
            Assertions.assertEquals(Files.size(file), out.getContentLength(), "File size differs from from the output size.");
            // Check the input stream, should contain all "a"'s
            try (InputStream in = out.toInputStream()) {
                int b;
                int i = 0;
                while ((b = in.read()) != -1) {
                    Assertions.assertEquals('a', (char) b, String.format("Byte at %d was not 'a', but '%s'.", i, b));
                    i++;
                }
                Assertions.assertEquals(len, i, String.format("Expected %d bytes to be read, but %d were read.", len, i));
            }
        }
    }

    /**
     * Tests that writing more bytes than the file threshold throws an exception.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void fileThresholdHit() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "1B");
        System.setProperty(Options.ENTITY_FILE_THRESHOLD.name(), "10B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            Path file = null;
            try {
                for (int i = 0; i < len; i++) {
                    out.write('a');
                    if (file == null && i > 1) {
                        file = out.getFile();
                    }
                }
                Assertions.fail("File threshold should have been hit");
            } catch (IllegalStateException ignore) {
            }
            // We should have been written to a file
            Assertions.assertNotNull(file, "Expected data to be written to a file.");
            Assertions.assertTrue(out.isClosed(), "Expected the output stream to be closed");
            Assertions.assertTrue(Files.notExists(file), "Expected the file to be deleted");
        }
    }

    /**
     * Tests writing with no threshold set.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void fileThresholdNoLimit() throws Exception {
        System.setProperty(Options.ENTITY_FILE_THRESHOLD.name(), "-1");
        final long size = 60L;
        try (
                EntityOutputStream out = new EntityOutputStream();
                BufferedOutputStream buffered = new BufferedOutputStream(out)) {
            // This will be a very large file, we'll write it in chunks instead of a byte at a time
            final long len = SizeUnit.MEGABYTE.toBytes(size);
            for (long i = 0; i < len; i++) {
                buffered.write('a');
            }
            buffered.flush();
            // We should have been written to a file
            final Path file = out.getFile();
            final long fileSize = Files.size(file);
            Assertions.assertNotNull(file, "Expected data to be written to a file.");
            Assertions.assertEquals(0, out.getAndClearMemory().length, "Expected the memory to be cleared");
            Assertions.assertEquals(fileSize, out.getContentLength(), "File size differs from from the output size.");
            Assertions.assertEquals(SizeUnit.MEGABYTE.toBytes(size), fileSize,
                    String.format("Expected %s got %s", SizeUnit.toHumanReadable(SizeUnit.MEGABYTE.toBytes(size)),
                            SizeUnit.toHumanReadable(fileSize)));
            // Just consume the InputStream in order to delete the file
            try (InputStream in = out.toInputStream()) {
                Assertions.assertNotNull(in);
            }
        }
    }

    /**
     * Tests writing a single byte to a closed stream will fail.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeByteToClosedStream() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            out.close();
            // The stream should be closed now
            Assertions.assertTrue(out.isClosed(), "The stream was not closed.");
            // This should fail to write as the stream is closed
            Assertions.assertThrows(IllegalStateException.class, () -> out.write('x'));
        }
    }

    /**
     * Tests writing a byte array to a closed stream will fail.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeByteArrayToClosedStream() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            out.close();
            // The stream should be closed now
            Assertions.assertTrue(out.isClosed(), "The stream was not closed.");
            // This should fail to write as the stream is closed
            Assertions.assertThrows(IllegalStateException.class, () -> out.write(bytes));
        }
    }

    /**
     * Tests writing a byte array with an offset to a closed stream will fail.
     *
     * @throws Exception if a test failure occurs
     */
    @Test
    public void writeByteArrayWithOffsetToClosedStream() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            out.close();
            // The stream should be closed now
            Assertions.assertTrue(out.isClosed(), "The stream was not closed.");
            // This should fail to write as the stream is closed
            Assertions.assertThrows(IllegalStateException.class, () -> out.write(bytes, 0, 10));

        }
    }
}
