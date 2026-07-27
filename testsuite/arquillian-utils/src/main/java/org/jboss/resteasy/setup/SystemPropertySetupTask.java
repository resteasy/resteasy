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

package org.jboss.resteasy.setup;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;

/**
 * A setup task for Arquillian tests which set system properties in WildFly and remove them when the test is complete.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class SystemPropertySetupTask implements ServerSetupTask {

    private final Map<String, String> properties;
    private final Deque<ModelNode> tearDownOps;

    /**
     * Creates a new setup task which defines the provided properties.
     *
     * @param properties the properties to add
     */
    protected SystemPropertySetupTask(final Map<String, String> properties) {
        this.properties = properties;
        this.tearDownOps = new ArrayDeque<>();
    }

    @Override
    public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
        boolean opsAdded = false;
        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            final ModelNode address = Operations.createAddress("system-property", entry.getKey());
            if (createAddOrUpdateOp(managementClient.getControllerClient(), address, builder, entry.getValue())) {
                opsAdded = true;
            }
        }
        if (opsAdded) {
            executeOperation(managementClient, builder.build(),
                    (result) -> String.format("Failed to add system properties %s%n%s", properties,
                            Operations.getFailureDescription(result)
                                    .asString()));
        }
    }

    @Override
    public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();
        ModelNode revertOp;
        while ((revertOp = tearDownOps.pollLast()) != null) {
            builder.addStep(revertOp);
        }
        executeOperation(managementClient, builder.build(),
                (result) -> String.format("Failed to remove system properties %s%n%s", properties,
                        Operations.getFailureDescription(result)
                                .asString()));
    }

    private boolean createAddOrUpdateOp(final ModelControllerClient client, final ModelNode address,
            final CompositeOperationBuilder builder, final String value) throws IOException {
        ModelNode op = Operations.createReadResourceOperation(address);
        final ModelNode response = client.execute(op);
        if (Operations.isSuccessfulOutcome(response)) {
            final ModelNode model = Operations.readResult(response);
            if (model.hasDefined("value")) {
                final String currentValue = model.get("value").asString();
                if (!currentValue.equals(value)) {
                    builder.addStep(Operations.createWriteAttributeOperation(address, "value", value));
                    tearDownOps.add(Operations.createWriteAttributeOperation(address, "value", currentValue));
                    return true;
                }
                return false;
            } else {
                throw new RuntimeException(String.format("Could not find value in %s", response));
            }
        } else {
            op = Operations.createAddOperation(address);
            op.get("value").set(value);
            builder.addStep(op);
            tearDownOps.add(Operations.createRemoveOperation(address));
            return true;
        }
    }
}
