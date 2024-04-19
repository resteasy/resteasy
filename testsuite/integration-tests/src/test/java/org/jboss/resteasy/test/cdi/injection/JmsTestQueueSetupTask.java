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

package org.jboss.resteasy.test.cdi.injection;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.setup.SnapshotServerSetupTask;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class JmsTestQueueSetupTask extends SnapshotServerSetupTask {
    @Override
    protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();

        ModelNode address = Operations.createAddress("subsystem", "messaging-activemq", "server", "default");
        builder.addStep(Operations.createWriteAttributeOperation(address, "security-enabled", false));

        address = Operations.createAddress("subsystem", "messaging-activemq", "server", "default", "jms-queue", "test");
        final ModelNode op = Operations.createAddOperation(address);
        final ModelNode entries = op.get("entries")
                .setEmptyList();
        entries.add("java:/jms/queue/test");
        builder.addStep(op);

        final ModelNode result = client.getControllerClient()
                .execute(builder.build());
        if (!Operations.isSuccessfulOutcome(result)) {
            throw new RuntimeException(Operations.getFailureDescription(result)
                    .asString());
        }

    }
}
