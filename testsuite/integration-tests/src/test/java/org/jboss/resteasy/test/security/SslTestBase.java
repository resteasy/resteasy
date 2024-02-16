package org.jboss.resteasy.test.security;

import static org.jboss.resteasy.utils.PortProviderUtil.isIpv6;

import java.io.File;

import jakarta.ws.rs.client.Client;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.client.helpers.Operations.CompositeOperationBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.ServerReload;
import org.jboss.resteasy.utils.TestManagementClient;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.jupiter.api.Assertions;

/**
 * Base class for SSL tests. Contains utility methods used in all ssl tests.
 */
public abstract class SslTestBase {

    protected static Client client;
    protected static ResteasyClientBuilder resteasyClientBuilder;

    protected static String RESOURCES = "src/test/resources/org/jboss/resteasy/test/security/";
    protected static final String PASSWORD = "123456";
    protected static final String DEPLOYMENT_NAME = "ssl-war";
    protected static final String HOSTNAME = "localhost";

    @ArquillianResource
    protected static ContainerController containerController;

    @ArquillianResource
    protected static Deployer deployer;

    /**
     * Generate a https URL
     *
     * @return a full https URL
     */
    protected static String generateHttpsURL(int offset, String hostname) {
        // ipv4
        if (!isIpv6()) {
            return String.format("https://%s:%d/%s%s", hostname, 8443 + offset, DEPLOYMENT_NAME, "/ssl/hello");
        }
        // ipv6
        return String.format("https://[%s]:%d/%s%s", hostname, 8443 + offset, DEPLOYMENT_NAME, "/ssl/hello");
    }

    /**
     * Generate a https URL, use localhost
     */
    protected static String generateHttpsURL(int offset) {
        return generateHttpsURL(offset, true);
    }

    /**
     * Generate a https URL, allow to use localhost or hostname from node property
     */
    protected static String generateHttpsURL(int offset, boolean forceLocalhost) {
        if (forceLocalhost) {
            return generateHttpsURL(offset, HOSTNAME);
        } else {
            return generateHttpsURL(offset, PortProviderUtil.getHost());
        }
    }

    /**
     * Set up ssl in jboss-cli so https endpoint can be accessed only if client trusts certificates in the server keystore.
     *
     * @throws Exception
     */
    protected static void secureServer(String serverKeystorePath, int portOffset) throws Exception {
        File file = new File(serverKeystorePath);
        serverKeystorePath = file.getAbsolutePath();

        if (TestUtil.isWindows()) {
            serverKeystorePath = serverKeystorePath.replace("\\", "\\\\");
        }

        try (ModelControllerClient client = TestManagementClient.create(TestUtil.getManagementHost(),
                TestUtil.getManagementPort(portOffset))) {
            final CompositeOperationBuilder builder = CompositeOperationBuilder.create();

            final ModelNode ksAddress = Operations.createAddress("subsystem", "elytron", "key-store", "applicationKS");
            builder.addStep(Operations.createWriteAttributeOperation(ksAddress, "path", serverKeystorePath));
            builder.addStep(Operations.createUndefineAttributeOperation(ksAddress, "relative-to"));

            final ModelNode password = new ModelNode();
            password.get("clear-text").set(PASSWORD);
            builder.addStep(Operations.createWriteAttributeOperation(ksAddress, "credential-reference", password));

            final ModelNode kmAddress = Operations.createAddress("subsystem", "elytron", "key-manager", "applicationKM");
            builder.addStep(Operations.createWriteAttributeOperation(kmAddress, "credential-reference", password));

            final ModelNode utAddress = Operations.createAddress("subsystem", "undertow", "server", "default-server",
                    "https-listener", "https");
            builder.addStep(Operations.createUndefineAttributeOperation(utAddress, "security-realm"));
            builder.addStep(Operations.createWriteAttributeOperation(utAddress, "ssl-context", "applicationSSC"));

            final ModelNode result = client.execute(builder.build());
            if (!Operations.isSuccessfulOutcome(result)) {
                Assertions.fail("Failed to configure SSL context: " + Operations.getFailureDescription(result).asString());
            }

            ServerReload.reloadIfRequired(client);
        }
    }

}
