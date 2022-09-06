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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class EntityOutputStreamTestCase {

    private Properties currentProperties;

    @Before
    public void captureProperties() {
        currentProperties = System.getProperties();
        System.setProperties(new Properties(currentProperties));
    }

    @After
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
                BufferedOutputStream buffered = new BufferedOutputStream(out);
        ) {
            final int len = (int) SizeUnit.MEGABYTE.toBytes(4L);
            for (int i = 0; i < len; i++) {
                buffered.write('a');
            }
            buffered.flush();
            // We should have been kept in memory
            final Path file = out.getFile();
            Assert.assertNull("Expected data to be kept in memory.", file);
            Assert.assertEquals("Memory size differs from from the output size.", len, out.getContentLength());
            // Just consume the InputStream in order to delete the file
            try (InputStream in = out.toInputStream()) {
                Assert.assertNotNull(in);
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
            Assert.assertNull("Expected data to be kept in memory.", file);
            Assert.assertEquals("Memory size differs from from the output size.", len, out.getContentLength());
            // Check the input stream, should contain all "a"'s
            try (InputStream in = out.toInputStream()) {
                // The in-memory data should be cleared now
                Assert.assertEquals("Memory should have be cleared", 0, out.getContentLength());
                Assert.assertEquals("Memory should have be cleared", 0, out.getAndClearMemory().length);
                int b;
                int i = 0;
                while ((b = in.read()) != -1) {
                    Assert.assertEquals(String.format("Byte at %d was not 'a', but '%s'.", i, b), 'a', (char) b);
                    i++;
                }
                Assert.assertEquals(String.format("Expected %d bytes to be read, but %d were read.", len, i), len, i);
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
                BufferedOutputStream buffered = new BufferedOutputStream(out);
        ) {
            final int len = (int) SizeUnit.MEGABYTE.toBytes(10L);
            for (int i = 0; i < len; i++) {
                buffered.write('a');
            }
            buffered.flush();
            // We should have been written to a file
            final Path file = out.getFile();
            Assert.assertNotNull("Expected data to be written to a file.", file);
            Assert.assertEquals("Expected the memory to be cleared", 0, out.getAndClearMemory().length);
            Assert.assertEquals("File size differs from from the output size.", Files.size(file), out.getContentLength());
            // Just consume the InputStream in order to delete the file
            try (InputStream in = out.toInputStream()) {
                Assert.assertNotNull(in);
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
            Assert.assertNotNull("Expected data to be written to a file.", file);
            Assert.assertEquals("Expected the memory to be cleared", 0, out.getAndClearMemory().length);
            Assert.assertEquals("File size differs from from the output size.", Files.size(file), out.getContentLength());
            // Check the input stream, should contain all "a"'s
            try (InputStream in = out.toInputStream()) {
                int b;
                int i = 0;
                while ((b = in.read()) != -1) {
                    Assert.assertEquals(String.format("Byte at %d was not 'a', but '%s'.", i, b), 'a', (char) b);
                    i++;
                }
                Assert.assertEquals(String.format("Expected %d bytes to be read, but %d were read.", len, i), len, i);
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
                Assert.fail("File threshold should have been hit");
            } catch (IllegalStateException ignore) {
            }
            // We should have been written to a file
            Assert.assertNotNull("Expected data to be written to a file.", file);
            Assert.assertTrue("Expected the output stream to be closed", out.isClosed());
            Assert.assertTrue("Expected the file to be deleted", Files.notExists(file));
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
                BufferedOutputStream buffered = new BufferedOutputStream(out)
        ) {
            // This will be a very large file, we'll write it in chunks instead of a byte at a time
            final long len = SizeUnit.MEGABYTE.toBytes(size);
            for (long i = 0; i < len; i++) {
                buffered.write('a');
            }
            buffered.flush();
            // We should have been written to a file
            final Path file = out.getFile();
            final long fileSize = Files.size(file);
            Assert.assertNotNull("Expected data to be written to a file.", file);
            Assert.assertEquals("Expected the memory to be cleared", 0, out.getAndClearMemory().length);
            Assert.assertEquals("File size differs from from the output size.", fileSize, out.getContentLength());
            Assert.assertEquals(String.format("Expected %s got %s", SizeUnit.toHumanReadable(SizeUnit.MEGABYTE.toBytes(size)), SizeUnit.
                            toHumanReadable(fileSize)),
                    SizeUnit.MEGABYTE.toBytes(size), fileSize);
            // Just consume the InputStream in order to delete the file
            try (InputStream in = out.toInputStream()) {
                Assert.assertNotNull(in);
            }
        }
    }

    /**
     * Tests writing a single byte to a closed stream will fail.
     *
     * @throws Exception if a test failure occurs
     */
    @Test(expected = IllegalStateException.class)
    public void writeByteToClosedStream() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            out.close();
            // The stream should be closed now
            Assert.assertTrue("The stream was not closed.", out.isClosed());
            // This should fail to write as the stream is closed
            out.write('x');
        }
    }

    /**
     * Tests writing a byte array to a closed stream will fail.
     *
     * @throws Exception if a test failure occurs
     */
    @Test(expected = IllegalStateException.class)
    public void writeByteArrayToClosedStream() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            out.close();
            // The stream should be closed now
            Assert.assertTrue("The stream was not closed.", out.isClosed());
            // This should fail to write as the stream is closed
            out.write(bytes);
        }
    }

    /**
     * Tests writing a byte array with an offset to a closed stream will fail.
     *
     * @throws Exception if a test failure occurs
     */
    @Test(expected = IllegalStateException.class)
    public void writeByteArrayWithOffsetToClosedStream() throws Exception {
        System.setProperty(Options.ENTITY_MEMORY_THRESHOLD.name(), "30B");
        try (EntityOutputStream out = new EntityOutputStream()) {
            final int len = 20;
            final byte[] bytes = new byte[len];
            Arrays.fill(bytes, (byte) 'a');
            out.write(bytes);
            out.close();
            // The stream should be closed now
            Assert.assertTrue("The stream was not closed.", out.isClosed());
            // This should fail to write as the stream is closed
            out.write(bytes, 0, 10);
        }
    }
}
