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
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.Operation;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;

/**
 * A setup task for configuring loggers for tests.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class LoggingSetupTask extends SnapshotServerSetupTask implements ServerSetupTask {
    private static final Map<String, Set<String>> DEFAULT_LOG_LEVELS = Map.of("DEBUG",
            Collections.singleton("org.jboss.resteasy"));

    @Override
    protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();
        final String consoleHandlerName = getConsoleHandlerName();
        if (consoleHandlerName != null) {
            final ModelNode address = Operations.createAddress("subsystem", "logging", "console-handler", consoleHandlerName);
            builder.addStep(Operations.createWriteAttributeOperation(address, "level", "ALL"));
        }
        for (Map.Entry<String, Set<String>> entry : getLogLevels().entrySet()) {
            for (String logger : entry.getValue()) {
                final ModelNode address;
                if (logger.isBlank()) {
                    address = Operations.createAddress("subsystem", "logging", "root-logger", "ROOT");
                } else {
                    address = Operations.createAddress("subsystem", "logging", "logger", logger);
                }
                final ModelNode op;
                if (loggerExists(client.getControllerClient(), address)) {
                    op = Operations.createWriteAttributeOperation(address, "level", entry.getKey());
                } else {
                    op = Operations.createAddOperation(address);
                    op.get("level").set(entry.getKey());
                }
                builder.addStep(op);
            }
        }
        executeOp(client.getControllerClient(), builder.build());
    }

    private boolean loggerExists(final ModelControllerClient client, final ModelNode address) throws IOException {
        final ModelNode op = Operations.createReadResourceOperation(address);
        final ModelNode result = client.execute(op);
        return Operations.isSuccessfulOutcome(result);
    }

    private void executeOp(final ModelControllerClient client, final Operation op) throws IOException {
        final ModelNode result = client.execute(op);
        if (!Operations.isSuccessfulOutcome(result)) {
            throw new RuntimeException(Operations.getFailureDescription(result).asString());
        }
    }

    /**
     * Determines the name of the console handler. If {@code null} is returned the console handler will not be changed.
     *
     * @return the console handler name or {@code null} to configure nothing
     */
    protected String getConsoleHandlerName() {
        return "CONSOLE";
    }

    /**
     * A map where the key is the log level and the value is a collection of logger names. This map is used to create
     * loggers. If the logger already exists it will be updated instead.
     *
     * @return a mapping of levels to loggers
     */
    protected Map<String, Set<String>> getLogLevels() {
        return DEFAULT_LOG_LEVELS;
    }
}
