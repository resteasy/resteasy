/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.utils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;

/**
 * Utilities for handling server reloads.
 *
 * @author Stuart Douglas
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("unused")
public class ServerReload {
    /**
     * Default time, in ms, to wait for reload to complete.
     */
    public static final int TIMEOUT = 100000;
    private static final ModelNode EMPTY_ADDRESS = new ModelNode().setEmptyList();

    /**
     * Reloads the server and returns immediately.
     *
     * @param client the client used to execute the reload operation
     */
    public static void executeReload(final ModelControllerClient client) {
        executeReload(client, Operations.createOperation("reload"));
    }

    /**
     * Reloads the server and returns immediately.
     *
     * @param client   the client used to execute the reload operation
     * @param reloadOp the reload operation to execute
     */
    public static void executeReload(final ModelControllerClient client, final ModelNode reloadOp) {
        try {
            final ModelNode result = client.execute(reloadOp);
            if (!Operations.isSuccessfulOutcome(result)) {
                Assert.fail(Operations.getFailureDescription(result)
                        .asString());
            }
        } catch (IOException e) {
            final Throwable cause = e.getCause();
            if (!(cause instanceof ExecutionException) && !(cause instanceof CancellationException)) {
                throw new RuntimeException(e);
            } // else ignore, this might happen if the channel gets closed before we got the response
        }
    }

    /**
     * Executes a {@code reload} operation and waits the {@link #TIMEOUT default timeout}
     * for the reload to complete.
     *
     * @param client the client to use for the request. Cannot be {@code null}
     *
     * @throws AssertionError if the reload does not complete within the timeout
     */
    public static void executeReloadAndWaitForCompletion(final ModelControllerClient client) {
        executeReloadAndWaitForCompletion(client, TIMEOUT);
    }

    /**
     * Executes a {@code reload} operation and waits the {@link #TIMEOUT default timeout}
     * for the reload to complete.
     *
     * @param client   the client to use for the request. Cannot be {@code null}
     * @param reloadOp the operation used for the reload
     *
     * @throws AssertionError if the reload does not complete within the timeout
     */
    public static void executeReloadAndWaitForCompletion(final ModelControllerClient client, final ModelNode reloadOp) {
        executeReloadAndWaitForCompletion(client, reloadOp, TIMEOUT);
    }

    /**
     * Executes a {@code reload} operation and waits a configurable maximum time for the reload to complete.
     *
     * @param client  the client to use for the request. Cannot be {@code null}
     * @param timeout maximum time to wait for the reload to complete, in milliseconds
     *
     * @throws AssertionError if the reload does not complete within the specified timeout
     */
    public static void executeReloadAndWaitForCompletion(final ModelControllerClient client, final int timeout) {
        executeReload(client);
        final ModelControllerClientConfiguration config = (client instanceof TestManagementClient)
                ? ((TestManagementClient) client).getConfiguration()
                : TestManagementClient.createDefaultConfig();
        waitForLiveServerToReload(timeout, config);
    }

    /**
     * Executes a {@code reload} operation and waits a configurable maximum time for the reload to complete.
     *
     * @param client   the client to use for the request. Cannot be {@code null}
     * @param reloadOp the operation used for the reload
     * @param timeout  maximum time to wait for the reload to complete, in milliseconds
     *
     * @throws AssertionError if the reload does not complete within the specified timeout
     */
    public static void executeReloadAndWaitForCompletion(final ModelControllerClient client, final ModelNode reloadOp,
            final int timeout) {
        executeReload(client, reloadOp);
        final ModelControllerClientConfiguration config = (client instanceof TestManagementClient)
                ? ((TestManagementClient) client).getConfiguration()
                : TestManagementClient.createDefaultConfig();
        waitForLiveServerToReload(timeout, config);
    }

    /**
     * Executes a {@code reload} operation and waits a configurable maximum time for the reload to complete.
     *
     * @param client       the client to use for the request. Cannot be {@code null}
     * @param serverConfig the server configuration file
     *
     * @throws AssertionError if the reload does not complete within the specified timeout
     */
    public static void executeReloadAndWaitForCompletion(final ModelControllerClient client,
            final String serverConfig) {
        final ModelNode op = Operations.createOperation("reload");
        if (serverConfig != null) {
            op.get("server-config")
                    .set(serverConfig);
        }
        executeReloadAndWaitForCompletion(client, op);
    }

    /**
     * Returns the current running state, {@code server-state}, of the server.
     *
     * @param client the client used to execute the operation
     *
     * @return the running state or "failed" if the operation was unsuccessful
     *
     * @throws IOException if a communication error occurs
     */
    public static String getContainerRunningState(final ModelControllerClient client) throws IOException {
        final ModelNode rsp = client.execute(Operations.createReadAttributeOperation(EMPTY_ADDRESS, "server-state"));
        return Operations.isSuccessfulOutcome(rsp) ? Operations.readResult(rsp)
                .asString() : "failed";
    }

    /**
     * Checks if the container status is "reload-required" and if it's the case executes reload and waits for completion.
     *
     * @param client the client used to execute the operation
     *
     * @throws IOException if a communication error occurs
     */
    public static void reloadIfRequired(final ModelControllerClient client) throws Exception {
        final String runningState = getContainerRunningState(client);
        if ("reload-required".equalsIgnoreCase(runningState)) {
            executeReloadAndWaitForCompletion(client);
        } else {
            Assert.assertEquals("Server state 'running' is expected", "running", runningState);
        }
    }

    /**
     * Takes a snapshot of the current state of the server.
     * <p>
     * Returns a AutoCloseable that can be used to restore the server state
     * </p>
     *
     * @param port the port to create the client for
     *
     * @return A closeable that can be used to restore the server
     */
    public static AutoCloseable takeSnapshot(final int port) {
        try (ModelControllerClient client = TestManagementClient.create("localhost", port)) {
            final String fileName = takeSnapshot0(client);
            return () -> {
                try (ModelControllerClient restoreClient = TestManagementClient.create("localhost", port)) {
                    executeReloadAndWaitForCompletion(restoreClient, fileName);

                    final ModelNode result1 = restoreClient.execute(Operations.createOperation("write-config"));
                    if (!Operations.isSuccessfulOutcome(result1)) {
                        Assert.fail(
                                "Failed to write config after restoring from snapshot "
                                        + Operations.getFailureDescription(result1)
                                                .asString());
                    }
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to take snapshot", e);
        }
    }

    /**
     * Takes a snapshot of the current state of the server.
     * <p>
     * Returns a AutoCloseable that can be used to restore the server state
     * </p>
     *
     * @param client The client
     *
     * @return A closeable that can be used to restore the server
     */
    public static AutoCloseable takeSnapshot(final ModelControllerClient client) {
        try {
            final String fileName = takeSnapshot0(client);
            return () -> {
                executeReloadAndWaitForCompletion(client, fileName);

                final ModelNode result1 = client.execute(Operations.createOperation("write-config"));
                if (!Operations.isSuccessfulOutcome(result1)) {
                    Assert.fail(
                            "Failed to write config after restoring from snapshot " + Operations.getFailureDescription(result1)
                                    .asString());
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to take snapshot", e);
        }
    }

    private static String takeSnapshot0(final ModelControllerClient client) throws IOException {
        final ModelNode op = Operations.createOperation("take-snapshot");
        final ModelNode result = client.execute(op);
        if (!Operations.isSuccessfulOutcome(result)) {
            Assert.fail("Reload operation didn't finish successfully: " + Operations.getFailureDescription(result)
                    .asString());
        }
        final String snapshot = Operations.readResult(result)
                .asString();
        return snapshot.contains(File.separator)
                ? snapshot.substring(snapshot.lastIndexOf(File.separator) + 1)
                : snapshot;
    }

    @SuppressWarnings("BusyWait")
    private static void waitForLiveServerToReload(final int timeout, final ModelControllerClientConfiguration config) {
        final long start = System.currentTimeMillis();
        final ModelNode operation = Operations.createReadAttributeOperation(EMPTY_ADDRESS, "server-state");
        while (System.currentTimeMillis() - start < timeout) {
            //do the sleep before we check, as the attribute state may not change instantly
            //also reload generally takes longer than 100ms anyway
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }
            try (
                    ModelControllerClient liveClient = ModelControllerClient.Factory.create(config)) {
                try {
                    final ModelNode result = liveClient.execute(operation);
                    if (Operations.isSuccessfulOutcome(result) && "running".equals(Operations.readResult(result)
                            .asString())) {
                        return;
                    }
                } catch (IOException ignore) {
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        fail("Live Server did not reload in the imparted time.");
    }
}
