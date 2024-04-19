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

package org.jboss.resteasy.test.client.authentication;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.setup.SnapshotServerSetupTask;
import org.jboss.as.controller.client.Operation;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AuthenticationSetupTask extends SnapshotServerSetupTask implements ServerSetupTask {
    private static final String[] MECHANISMS = {
            "BASIC",
            "DIGEST",
            "DIGEST-SHA-256",
            "DIGEST-SHA-512-256",
    };

    private Path configDir;

    @Override
    protected void doSetup(final ManagementClient client, final String containerId) throws Exception {
        final String sdName = TestAuth.SECURITY_DOMAIN;
        final String realmName = TestAuth.REALM_NAME;

        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();

        // Add the realm
        final ModelNode realmAddress = Operations.createAddress("subsystem", "elytron", "filesystem-realm", realmName);
        ModelNode op = Operations.createAddOperation(realmAddress);
        op.get("path").set(realmName);
        op.get("relative-to").set("jboss.server.config.dir");
        builder.addStep(op);
        addUser(builder, realmAddress, TestAuth.USER_1, TestAuth.PASSWORD_1);
        addUser(builder, realmAddress, TestAuth.USER_2, TestAuth.PASSWORD_2);

        // Configure the role decoder
        final ModelNode roleDecoder = Operations.createAddress("subsystem", "elytron", "simple-role-decoder",
                "from-roles-attribute-" + realmName);
        op = Operations.createAddOperation(roleDecoder);
        op.get("attribute").set("roles");
        builder.addStep(op);

        // Configure the security domain
        final ModelNode sdAddress = Operations.createAddress("subsystem", "elytron", "security-domain", sdName);
        op = Operations.createAddOperation(sdAddress);
        final ModelNode realm = new ModelNode();
        realm.get("realm").set(realmName);
        realm.get("role-decoder").set("from-roles-attribute-" + realmName);
        final ModelNode realms = op.get("realms").setEmptyList();
        realms.add(realm);
        op.get("default-realm").set(realmName);
        op.get("permission-mapper").set("default-permission-mapper");
        builder.addStep(op);

        // Configure the authentication factory
        final ModelNode httpAuthFactoryAddress = Operations.createAddress("subsystem", "elytron", "http-authentication-factory",
                "http-auth-" + realmName);
        op = Operations.createAddOperation(httpAuthFactoryAddress);
        op.get("security-domain").set(sdName);
        op.get("http-server-mechanism-factory").set("global");
        final ModelNode mechanisms = op.get("mechanism-configurations").setEmptyList();
        for (String mechanismName : MECHANISMS) {
            final ModelNode mechanism = new ModelNode().setEmptyObject();
            mechanism.get("mechanism-name").set(mechanismName);
            final ModelNode mechanismRealmConfigs = mechanism.get("mechanism-realm-configurations").setEmptyList();
            mechanismRealmConfigs.add("realm-name", realmName);
            mechanisms.add(mechanism);
        }
        builder.addStep(op);

        // Configure Undertow
        final ModelNode undertowAddress = Operations.createAddress("subsystem", "undertow", "application-security-domain",
                sdName);
        op = Operations.createAddOperation(undertowAddress);
        op.get("http-authentication-factory").set("http-auth-" + realmName);
        builder.addStep(op);

        executeOp(client, builder.build());

        // Get the configuration directory
        final ModelNode address = Operations.createAddress("path", "jboss.server.config.dir");
        op = Operations.createOperation("path-info", address);
        final ModelNode result = executeOp(client, Operation.Factory.create(op));
        configDir = Path.of(result.get("path", "resolved-path").asString(), realmName);
    }

    @Override
    protected void nonManagementCleanUp() throws Exception {
        Files.walkFileTree(configDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    ModelNode executeOp(final ManagementClient managementClient, final Operation op) throws IOException {
        final ModelNode result = managementClient.getControllerClient().execute(op);
        if (!Operations.isSuccessfulOutcome(result)) {
            throw new RuntimeException(Operations.getFailureDescription(result).asString() + "\n" + op.getOperation());
        }
        return Operations.readResult(result);
    }

    private void addUser(final CompositeOperationBuilder builder, final ModelNode address, final String username,
            final String password) {
        ModelNode op = Operations.createOperation("add-identity", address);
        op.get("identity").set(username);
        builder.addStep(op);
        op = Operations.createOperation("set-password", address);
        op.get("identity").set(username);
        final ModelNode pwd = op.get("clear").setEmptyObject();
        pwd.get("password").set(password);
        builder.addStep(op);
        op = Operations.createOperation("add-identity-attribute", address);
        op.get("identity").set(username);
        op.get("name").set("roles");
        final ModelNode roles = op.get("value").setEmptyList();
        roles.add("user");
        builder.addStep(op);
    }
}
