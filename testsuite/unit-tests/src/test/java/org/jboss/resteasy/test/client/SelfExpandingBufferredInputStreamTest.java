package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Unit test for SelfExpandingBufferredInputStream
 * @tpSince RESTEasy 3.0.16
 */
public class SelfExpandingBufferredInputStreamTest {
    String data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    InputStream input;
    SelfExpandingBufferredInputStream stream;
    InputStream control;

    @Before
    public void setUp() {
        input = new ByteArrayInputStream(data.getBytes());
        control = new ByteArrayInputStream(data.getBytes());
        stream = new SelfExpandingBufferredInputStream(input, 1);
    }

    /**
     * @tpTestDetails Test not supported mark
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testMarkNotSupported() {
        stream.mark(256);
    }

    /**
     * @tpTestDetails Test for buffer auto expands read
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBufferAutoExpandsRead() throws Exception {
        stream.read();
        assertEquals(1, stream.getBufSize());
        stream.read();
        assertEquals(2, stream.getBufSize());
        stream.read(new byte[2]);
        assertEquals(4, stream.getBufSize());
        stream.read(new byte[2]);
        assertEquals(8, stream.getBufSize());
        assertEquals(6, stream.getPos());
    }

    /**
     * @tpTestDetails Test for reset returns to position zero
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResetReturnsToPositionZero() throws Exception {
        stream.read(new byte[7]);
        assertEquals(8, stream.getBufSize());
        assertEquals(7, stream.getPos());
        stream.reset();
        assertEquals(8, stream.getBufSize());
        assertEquals(0, stream.getPos());
    }

    /**
     * @tpTestDetails Test for reset and read over buffer
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResetAndReadOverMarklengthContinuesToExpandBuffer() throws Exception {
        byte[] f7 = new byte[7];
        stream.read(f7);
        assertEquals(8, stream.getBufSize());

        stream.reset();
        assertEquals(8, stream.getBufSize());

        byte[] f13 = new byte[13];
        stream.read(f13);
        assertEquals(16, stream.getBufSize());
    }

    /**
     * @tpTestDetails Test for read and reset data
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReadResetReReadReturnsSameData() throws Exception {
        byte[] first = new byte[7];
        stream.read(first);

        stream.reset();
        stream.read(new byte[19]);
        stream.reset();

        byte[] second = new byte[7];
        stream.read(second);
        assertByteArrayEquals(first, second);
    }

    /**
     * @tpTestDetails Test for read offset
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReadOffsetExpandsBuffer() throws Exception {
        byte[] f7 = new byte[7];
        stream.read(f7, 1, 6);
        assertEquals(8, stream.getBufSize());
    }

    private void assertByteArrayEquals(byte[] first, byte[] second) {
        assertEquals(new String(first), new String(second));
    }
}