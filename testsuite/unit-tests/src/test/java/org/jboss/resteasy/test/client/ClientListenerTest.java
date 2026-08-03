package org.jboss.resteasy.test.client;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientListener;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests for ClientListener support in ResteasyClientBuilderImpl and ResteasyClientImpl
 * @tpSince RESTEasy 7.0
 */
public class ClientListenerTest {

    /**
     * @tpTestDetails Register a ClientListener via ClientBuilder.listener() and verify the method returns
     *                the same builder instance, enabling fluent chaining.
     * @tpPassCrit The same builder instance is returned by listener()
     * @tpSince RESTEasy 7.0
     */
    @Test
    public void listenerReturnsBuilder() {
        ResteasyClientBuilderImpl builder = (ResteasyClientBuilderImpl) ClientBuilder.newBuilder();
        ClientListener listener = new ClientListener() {
        };

        ClientBuilder returned = builder.listener(listener);

        Assertions.assertSame(builder, returned,
                "listener() must return the same builder instance");
        builder.build().close();
    }

    /**
     * @tpTestDetails Register a ClientListener via ClientBuilder.listener(), build a client, and close it.
     *                Verifies that ResteasyClientImpl invokes the callback and passes the correct client reference.
     * @tpPassCrit ClientListener.closed() is invoked and receives the same Client instance that was closed
     * @tpSince RESTEasy 7.0
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

        Client client = ClientBuilder.newBuilder().listener(listener).build();
        client.close();

        Assertions.assertTrue(called.get(),
                "ClientListener.closed() was not invoked by ResteasyClientImpl.close()");
        Assertions.assertSame(client, closedClient[0],
                "ClientListener.closed() did not receive the closed Client instance");
    }

    /**
     * @tpTestDetails Close a client twice. Verifies that ResteasyClientImpl guards against repeated close()
     *                calls so that the listener is only notified once.
     * @tpPassCrit ClientListener.closed() is invoked exactly once even when Client.close() is called twice
     * @tpSince RESTEasy 7.0
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
     * @tpSince RESTEasy 7.0
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
     *                build a second client. Verifies that listeners are snapshotted at build() time so that
     *                additions to the builder after build() do not affect already-built clients.
     * @tpPassCrit Listener added to builder after build() does not fire on the already-built client
     * @tpSince RESTEasy 7.0
     */
    @Test
    public void listenerSnapshotTakenAtBuildTime() {
        AtomicBoolean firstListenerCalled = new AtomicBoolean(false);
        AtomicBoolean secondListenerCalled = new AtomicBoolean(false);

        ClientListener firstListener = new ClientListener() {
            @Override
            public void closed(Client client) {
                firstListenerCalled.set(true);
            }
        };
        ClientListener secondListener = new ClientListener() {
            @Override
            public void closed(Client client) {
                secondListenerCalled.set(true);
            }
        };

        ResteasyClientBuilderImpl builder = new ResteasyClientBuilderImpl();
        builder.listener(firstListener);

        Client firstClient = builder.build();

        // add second listener AFTER first client was built
        builder.listener(secondListener);

        firstClient.close();

        Assertions.assertTrue(firstListenerCalled.get(),
                "First listener must be called on first client");
        Assertions.assertFalse(secondListenerCalled.get(),
                "Listener added to builder after build() must not fire on already-built client");

        builder.build().close();
    }
}
