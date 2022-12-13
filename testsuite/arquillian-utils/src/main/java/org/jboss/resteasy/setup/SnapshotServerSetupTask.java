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

package org.jboss.resteasy.setup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.utils.ServerReload;
import org.junit.Assert;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class SnapshotServerSetupTask implements ServerSetupTask {

    private final Map<String, AutoCloseable> snapshots = new HashMap<>();

    @Override
    public final void setup(final ManagementClient managementClient, final String containerId) throws Exception {
        snapshots.put(containerId, takeSnapshot(managementClient));
        doSetup(managementClient, containerId);
    }

    @Override
    public final void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
        final AutoCloseable snapshot = snapshots.remove(containerId);
        if (snapshot != null) {
            snapshot.close();
        }
        nonManagementCleanUp();
    }

    protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
    }

    @SuppressWarnings("RedundantThrows")
    protected void nonManagementCleanUp() throws Exception {
    }

    /**
     * Takes a snapshot of the current state of the server.
     *
     * Returns a AutoCloseable that can be used to restore the server state
     *
     * @param client The client
     *
     * @return A closeable that can be used to restore the server
     */
    private static AutoCloseable takeSnapshot(ManagementClient client) {
        try {
            final ModelNode op = Operations.createOperation("take-snapshot");
            final ModelNode result = client.getControllerClient()
                    .execute(op);
            if (!Operations.isSuccessfulOutcome(result)) {
                Assert.fail("Reload operation didn't finish successfully: " + Operations.getFailureDescription(result)
                        .asString());
            }
            final String snapshot = Operations.readResult(result)
                    .asString();
            final String fileName = snapshot.contains(File.separator)
                    ? snapshot.substring(snapshot.lastIndexOf(File.separator) + 1)
                    : snapshot;
            return () -> {
                executeReloadAndWaitForCompletion(client.getControllerClient(), fileName);

                final ModelNode result1 = client.getControllerClient()
                        .execute(Operations.createOperation("write-config"));
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

    private static void executeReloadAndWaitForCompletion(final ModelControllerClient client,
            final String serverConfig) {
        final ModelNode op = Operations.createOperation("reload");
        if (serverConfig != null) {
            op.get("server-config")
                    .set(serverConfig);
        }
        ServerReload.executeReloadAndWaitForCompletion(client, op);
    }
}
