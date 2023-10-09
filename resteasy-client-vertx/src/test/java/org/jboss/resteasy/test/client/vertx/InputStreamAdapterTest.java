package org.jboss.resteasy.test.client.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.jboss.resteasy.client.jaxrs.engines.vertx.InputStreamAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.test.core.TestUtils;
import io.vertx.test.fakestream.FakeStream;

public class InputStreamAdapterTest {

    private Vertx vertx;

    @BeforeEach
    public void setup() {
        vertx = Vertx.vertx();
    }

    @AfterEach
    public void after() {
        vertx.close();
    }

    @Test
    public void testConsumeSingleByteWaitsUntilDataBecomesAvailable() throws Exception {
        FakeStream<Buffer> stream = new FakeStream<>();
        InputStreamAdapter adapter = new InputStreamAdapter(stream);
        Thread th = Thread.currentThread();
        vertx.setTimer(10, id -> {
            while (th.getState() != Thread.State.WAITING) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                }
            }
            stream.emit(Buffer.buffer().appendByte((byte) 5));
        });
        int val = adapter.read();
        assertEquals(5, val);
    }

    @Test
    public void testPauseStreamStream() throws Exception {
        FakeStream<Buffer> stream = new FakeStream<>();
        InputStreamAdapter adapter = new InputStreamAdapter(stream);
        Buffer expected = TestUtils.randomBuffer(256 + 1);
        stream.emit(expected.slice(0, 256));
        assertFalse(stream.isPaused());
        stream.emit(expected.slice(256, 257));
        assertTrue(stream.isPaused());
        byte[] data = new byte[257];
        assertEquals(257, adapter.read(data));
        assertFalse(stream.isPaused());
    }

    @Test
    public void testEndStream1() throws Exception {
        FakeStream<Buffer> stream = new FakeStream<>();
        InputStreamAdapter adapter = new InputStreamAdapter(stream);
        stream.end();
        assertEquals(-1, adapter.read());
    }

    @Test
    public void testEndStream2() throws Exception {
        FakeStream<Buffer> stream = new FakeStream<>();
        InputStreamAdapter adapter = new InputStreamAdapter(stream);
        Thread th = Thread.currentThread();
        vertx.setTimer(10, id -> {
            while (th.getState() != Thread.State.WAITING) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                }
            }
            stream.end();
        });
        assertEquals(-1, adapter.read());
    }

    @Test
    public void testFailure1() throws Exception {
        FakeStream<Buffer> stream = new FakeStream<>();
        InputStreamAdapter adapter = new InputStreamAdapter(stream);
        Throwable cause = new Throwable();
        stream.fail(cause);
        try {
            adapter.read();
        } catch (IOException e) {
            assertSame(cause, e.getCause());
        }
    }

    @Test
    public void testFailure2() throws Exception {
        FakeStream<Buffer> stream = new FakeStream<>();
        InputStreamAdapter adapter = new InputStreamAdapter(stream);
        Throwable cause = new Throwable();
        Thread th = Thread.currentThread();
        vertx.setTimer(10, id -> {
            while (th.getState() != Thread.State.WAITING) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                }
            }
            stream.fail(cause);
        });
        try {
            adapter.read();
        } catch (IOException e) {
            assertSame(cause, e.getCause());
        }
    }
}
