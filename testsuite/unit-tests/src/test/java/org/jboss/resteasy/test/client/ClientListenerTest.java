package org.jboss.resteasy.test.client;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientListener;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests for ClientListener support in ResteasyClientBuilderImpl and ResteasyClientImpl
 * @tpSince RESTEasy 8.0
 */
public class ClientListenerTest {

    /**
     * @tpTestDetails Register a ClientListener via ClientBuilder.listener() and verify the method returns
     *                the same builder instance, enabling fluent chaining.
     * @tpPassCrit The same builder instance is returned by listener()
     * @tpSince RESTEasy 8.0
     */
    @Test
    public void listenerReturnsBuilder() {
        ClientBuilder builder = ClientBuilder.newBuilder();
        try {
            ClientListener listener = new ClientListener() {
            };

            ClientBuilder returned = builder.listener(listener);

            Assertions.assertSame(builder, returned,
                    "listener() must return the same builder instance");
        } finally {
            builder.build().close();
        }
    }

    /**
     * @tpTestDetails Register a ClientListener via ClientBuilder.listener(), build a client, and close it.
     *                Verifies that ResteasyClientImpl invokes the callback and passes the correct client reference.
     * @tpPassCrit ClientListener.closed() is invoked and receives the same Client instance that was closed
     * @tpSince RESTEasy 8.0
     */
    @Test
    public void listenerClosedInvokedOnClose() {
        AtomicBoolean called = new AtomicBoolean(false);
        Client[] closedClient = new Client[1];

        ClientListener listener = new ClientListener() {
            @Override
            public void closed(Client client) {
                called.set(true);
                closedClient[0] = client;
            }
        };

        Client client;
        try (Client c = ClientBuilder.newBuilder().listener(listener).build()) {
            client = c;
        }

        Assertions.assertTrue(called.get(),
                "ClientListener.closed() was not invoked by ResteasyClientImpl.close()");
        Assertions.assertSame(client, closedClient[0],
                "ClientListener.closed() did not receive the closed Client instance");
    }

    /**
     * @tpTestDetails Close a client twice. Verifies that ResteasyClientImpl guards against repeated close()
     *                calls so that the listener is only notified once.
     * @tpPassCrit ClientListener.closed() is invoked exactly once even when Client.close() is called twice
     * @tpSince RESTEasy 8.0
     */
    @Test
    public void listenerClosedInvokedOnlyOnce() {
        AtomicInteger count = new AtomicInteger();

        ClientListener listener = new ClientListener() {
            @Override
            public void closed(Client client) {
                count.incrementAndGet();
            }
        };

        Client client = ClientBuilder.newBuilder().listener(listener).build();
        client.close();
        client.close();

        Assertions.assertEquals(1, count.get(),
                "ClientListener.closed() must be invoked exactly once even if close() is called multiple times");
    }

    /**
     * @tpTestDetails Register a ClientListener and verify it is called after ResteasyClientImpl has already
     *                set its closed flag, i.e. ResteasyClient.isClosed() is true inside the callback.
     * @tpPassCrit ResteasyClient.isClosed() returns true when ClientListener.closed() is called
     * @tpSince RESTEasy 8.0
     */
    @Test
    public void listenerCalledAfterClientIsMarkedClosed() {
        AtomicBoolean isClosedDuringCallback = new AtomicBoolean(false);

        ClientListener listener = new ClientListener() {
            @Override
            public void closed(Client client) {
                isClosedDuringCallback.set(((ResteasyClient) client).isClosed());
            }
        };

        ClientBuilder.newBuilder().listener(listener).build().close();

        Assertions.assertTrue(isClosedDuringCallback.get(),
                "ResteasyClient.isClosed() must be true when ClientListener.closed() is called");
    }

    /**
     * @tpTestDetails Register a listener on the builder, build a first client, add a second listener, then
     *                build and close a second client. Verifies that listeners are snapshotted at build() time:
     *                the first listener fires for both clients (registered before either build), while the
     *                second listener fires only for the second client (registered after the first build).
     * @tpPassCrit First listener count is 2; second listener count is 1
     * @tpSince RESTEasy 8.0
     */
    @Test
    public void listenerSnapshotTakenAtBuildTime() {
        AtomicInteger firstListenerCount = new AtomicInteger();
        AtomicInteger secondListenerCount = new AtomicInteger();

        ClientListener firstListener = new ClientListener() {
            @Override
            public void closed(Client client) {
                firstListenerCount.incrementAndGet();
            }
        };
        ClientListener secondListener = new ClientListener() {
            @Override
            public void closed(Client client) {
                secondListenerCount.incrementAndGet();
            }
        };

        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.listener(firstListener);

        Client firstClient = builder.build();
        try {
            // add second listener AFTER first client was built
            builder.listener(secondListener);
        } finally {
            firstClient.close();
        }

        Assertions.assertEquals(1, firstListenerCount.get(),
                "First listener must be called once after first client is closed");
        Assertions.assertEquals(0, secondListenerCount.get(),
                "Listener added to builder after build() must not fire on already-built client");

        builder.build().close();

        Assertions.assertEquals(2, firstListenerCount.get(),
                "First listener must be called again when second client is closed");
        Assertions.assertEquals(1, secondListenerCount.get(),
                "Second listener must be called once when second client is closed");
    }
}
