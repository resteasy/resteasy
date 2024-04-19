package org.jboss.resteasy.setup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.arquillian.setup.ReloadServerSetupTask;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;

/**
 * This abstract class implements steps needed to create Elytron security domain.
 */
public abstract class AbstractUsersRolesSecurityDomainSetup extends ReloadServerSetupTask implements ServerSetupTask {

    // Properties file path
    private static final String USERS_FILENAME = "users.properties";
    private static final String ROLES_FILENAME = "roles.properties";

    private final URL userFile;
    private final URL rolesFile;
    private final Deque<ModelNode> toRemove;
    private final Queue<Path> filesToRemove;

    protected AbstractUsersRolesSecurityDomainSetup(final URL userFile, final URL rolesFile) {
        this.userFile = userFile;
        this.rolesFile = rolesFile;
        toRemove = new LinkedList<>();
        filesToRemove = new ArrayDeque<>();
    }

    @Override
    public void doSetup(ManagementClient client, String s) throws Exception {
        ModelNode address = Operations.createAddress("path", "jboss.server.config.dir");
        ModelNode op = Operations.createReadAttributeOperation(address, "path");
        final Path configDir = Path.of(executeOperation(client, op).asString());

        filesToRemove.add(createPropertiesFile(userFile, configDir.resolve(USERS_FILENAME)));
        filesToRemove.add(createPropertiesFile(rolesFile, configDir.resolve(ROLES_FILENAME)));

        // Create the operation builder
        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();

        for (Map.Entry<String, String> entry : getSecurityDomainConfig().entrySet()) {
            final String domainName = entry.getKey();
            final String realmName = entry.getValue();

            // Create Elytron properties-realm
            address = Operations.createAddress("subsystem", "elytron", "properties-realm", realmName);
            op = Operations.createAddOperation(address);
            final ModelNode userProperties = op.get("users-properties").setEmptyObject();
            userProperties.get("path").set(USERS_FILENAME);
            userProperties.get("relative-to").set("jboss.server.config.dir");
            userProperties.get("plain-text").set(true);

            final ModelNode groupProperties = op.get("groups-properties").setEmptyObject();
            groupProperties.get("path").set(ROLES_FILENAME);
            groupProperties.get("relative-to").set("jboss.server.config.dir");
            builder.addStep(op);
            toRemove.addLast(address);

            // Create Elytron security-domain
            address = Operations.createAddress("subsystem", "elytron", "security-domain", domainName);
            op = Operations.createAddOperation(address);
            final ModelNode realms = new ModelNode().setEmptyObject();
            realms.get("realm").set(realmName);
            realms.get("role-decoder").set("groups-to-roles");
            op.get("realms").setEmptyList().add(realms);

            op.get("default-realm").set(realmName);
            op.get("permission-mapper").set("default-permission-mapper");
            builder.addStep(op);
            toRemove.addFirst(address);

            // Create Elytron http-authentication-factory with previous security-domain
            address = Operations.createAddress("subsystem", "elytron", "http-authentication-factory",
                    "http-auth-" + domainName);
            op = Operations.createAddOperation(address);

            // Create the value for the mechanism-configurations
            final ModelNode mechanismConfigs = new ModelNode().setEmptyObject();
            mechanismConfigs.get("mechanism-name").set("BASIC");
            final ModelNode mechanisms = mechanismConfigs.get("mechanism-realm-configurations").setEmptyList();
            final ModelNode mechanismsValue = new ModelNode().setEmptyObject();
            mechanismsValue.get("realm-name").set("propRealm");
            mechanisms.add(mechanismsValue);

            op.get("mechanism-configurations").setEmptyList().add(mechanismConfigs);
            op.get("http-server-mechanism-factory").set("global");
            op.get("security-domain").set(domainName);
            builder.addStep(op);
            toRemove.addFirst(address);

            // Set undertow application-security-domain to the custom http-authentication-factory
            address = Operations.createAddress("subsystem", "undertow", "application-security-domain", domainName);
            op = Operations.createAddOperation(address);
            op.get("http-authentication-factory").set("http-auth-" + domainName);
            builder.addStep(op);
            toRemove.addFirst(address);
        }
        executeOperation(client, builder.build());
    }

    @Override
    public void doTearDown(ManagementClient client, String s) throws Exception {

        final CompositeOperationBuilder builder = CompositeOperationBuilder.create();
        ModelNode address;
        while ((address = toRemove.pollFirst()) != null) {
            builder.addStep(Operations.createRemoveOperation(address));
        }
        executeOperation(client, builder.build());

        // Clear any files that need to be
        Path file;
        while ((file = filesToRemove.poll()) != null) {
            Files.deleteIfExists(file);
        }
    }

    /**
     * A map of the security domain configuration. The key is the security domain name and the value is the realm name.
     *
     * <p>
     * Override this method to configure more than one security domain.
     * </p>
     *
     * @return the security domain configuration
     */
    public Map<String, String> getSecurityDomainConfig() {
        return Collections.singletonMap("jaxrsSecDomain", "propRealm");
    }

    private Path createPropertiesFile(final URL url, final Path file) throws IOException {
        if (url != null) {
            try (InputStream in = url.openStream()) {
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        }
        return file;
    }
}
