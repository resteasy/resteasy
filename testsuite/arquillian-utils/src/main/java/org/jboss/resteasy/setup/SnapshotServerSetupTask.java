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

import java.util.HashMap;
import java.util.Map;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.resteasy.utils.ServerReload;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class SnapshotServerSetupTask implements ServerSetupTask {

    private final Map<String, AutoCloseable> snapshots = new HashMap<>();

    @Override
    public final void setup(final ManagementClient managementClient, final String containerId) throws Exception {
        snapshots.put(containerId, ServerReload.takeSnapshot(managementClient.getControllerClient()));
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
}
