/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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

import java.io.IOException;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.utils.ServerReload;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AllowTraceMethodSetupTask implements ServerSetupTask {
    private final ModelNode address = Operations.createAddress("subsystem", "undertow", "server", "default-server",
            "http-listener", "default");

    @Override
    public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
        final ModelNode op = Operations.createOperation("list-remove", address);
        op.get("name").set("disallowed-methods");
        op.get("value").set("TRACE");
        executeOperation(managementClient, op);
        ServerReload.reloadIfRequired(managementClient.getControllerClient());
    }

    @Override
    public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
        final ModelNode op = Operations.createOperation("list-add", address);
        op.get("name").set("disallowed-methods");
        op.get("value").set("TRACE");
        executeOperation(managementClient, op);
        ServerReload.reloadIfRequired(managementClient.getControllerClient());
    }

    private static void executeOperation(final ManagementClient client, final ModelNode op) throws IOException {
        final ModelNode result = client.getControllerClient().execute(op);
        if (!Operations.isSuccessfulOutcome(result)) {
            throw new RuntimeException(String.format("Failed to execute op: %s%n%s", op,
                    Operations.getFailureDescription(result).asString()));
        }
    }
}
