package org.jboss.resteasy.test.providers.yaml;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.plugins.providers.YamlProvider;
import org.jboss.resteasy.test.providers.yaml.resource.AttackVector;
import org.jboss.resteasy.test.providers.yaml.resource.Message;
import org.jboss.resteasy.test.providers.yaml.resource.MessageResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test asserts that types can't be injected into YAML. This uses the {@link AttackVector} which simply sets a boolean
 * if the constructor was invoked. For this reason it's critical to reset the value before each test.
 * <p>
 * The attacks, in most cases, should fail to actually create the request resource. However, with CVE-2022-1471 the
 * type itself could be constructed via the {@link org.yaml.snakeyaml.constructor.Constructor}.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("deprecation")
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({BadActorYamlProviderTest.SystemPropertySetup.class})
public class BadActorYamlProviderTest {

    static class SystemPropertySetup implements ServerSetupTask {

        @Override
        public void setup(ManagementClient managementClient, String s) throws Exception {
            final ModelNode op = new ModelNode();
            op.get(ClientConstants.OP).set(ClientConstants.ADD);
            op.get(ClientConstants.OP_ADDR).add("system-property", YamlProvider.ALLOWED_LIST);
            op.get("value").set(Message.class.getName());
            managementClient.getControllerClient().execute(op);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String s) throws Exception {
            final ModelNode op = new ModelNode();
            op.get(ClientConstants.OP).set(ClientConstants.REMOVE_OPERATION);
            op.get(ClientConstants.OP_ADDR).add("system-property", YamlProvider.ALLOWED_LIST);
            managementClient.getControllerClient().execute(op);
        }
    }

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, BadActorYamlProviderTest.class.getSimpleName() + ".war")
                .addClasses(
                        TestApplication.class,
                        AttackVector.class,
                        Message.class,
                        MessageResource.class
                )
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    private static Client CLIENT;
    private static String ORIGINAL_ALLOWED_LIST_VALUE;

    @ArquillianResource
    private URI uri;

    @BeforeClass
    public static void setupClient() {
        // System property to allow deserialization of the Message class on the client side:
        ORIGINAL_ALLOWED_LIST_VALUE = System.setProperty(YamlProvider.ALLOWED_LIST, Message.class.getName());
        CLIENT = ClientBuilder.newClient();
    }

    @AfterClass
    public static void closeClient() {
        if (CLIENT != null) {
            CLIENT.close();
        }

        if (ORIGINAL_ALLOWED_LIST_VALUE == null) {
            System.clearProperty(YamlProvider.ALLOWED_LIST);
        } else {
            System.setProperty(YamlProvider.ALLOWED_LIST, ORIGINAL_ALLOWED_LIST_VALUE);
        }
    }

    @Before
    public void resetData() {
        try (Response response = CLIENT.target(generateUri("/message")).request().delete()) {
            Assert.assertEquals(Response.Status.NO_CONTENT, response.getStatusInfo());
        }
    }

    /**
     * Tests that a {@link Message} is successfully sent.
     */
    @Test
    public void postMessage() {
        final String yaml = "!!" +
                Message.class.getName() +
                " {text: passed}";
        try (
                Response response = CLIENT.target(generateUri("/message"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should have an CREATED response
            Assert.assertEquals(Response.Status.CREATED, response.getStatusInfo());
            // We should have a location in the headers
            final URI location = response.getLocation();
            Assert.assertNotNull("Expected a Location header in " + response.getHeaders(), location);

            // Check the response link, it should be "passed"
            try (Response getResponse = CLIENT.target(location).request("text/yaml").get()) {
                // We should have an OK response
                Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
                final Message message = getResponse.readEntity(Message.class);
                Assert.assertNotNull("Message should not be null", message);
                Assert.assertEquals("passed", message.getText());
            }
        }
    }

    /**
     * Tests that if the {@link AttackVector} is sent to the message end point, that it is not constructed.
     */
    @Test
    public void postMessageAttack() {
        final String yaml = "!!" +
                AttackVector.class.getName() +
                " {}";
        try (
                Response response = CLIENT.target(generateUri("/message"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should fail as the AttackVector should not be allowed
            checkExpectedStatus(response, Response.Status.BAD_REQUEST, Response.Status.INTERNAL_SERVER_ERROR);
            assertNotAttacked();
            assertEmpty();
        }
    }

    /**
     * Tests that a {@link Message} is successfully sent via a simple String entity. Given this is a string, the entity
     * itself is treated as string and set as the text on the {@link Message}.
     */
    @Test
    public void postMessageString() {
        final String yaml = "!!" +
                Message.class.getName() +
                " {text: \"passed string\"}";
        try (
                Response response = CLIENT.target(generateUri("/message/string"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should have an CREATED response
            Assert.assertEquals(Response.Status.CREATED, response.getStatusInfo());
            // We should have a location in the headers
            final URI location = response.getLocation();
            Assert.assertNotNull("Expected a Location header in " + response.getHeaders(), location);

            // Check the response link, it should be the yaml
            try (Response getResponse = CLIENT.target(location).request("text/yaml").get()) {
                // We should have an OK response
                Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
                final Message message = getResponse.readEntity(Message.class);
                Assert.assertNotNull("Message should not be null", message);
                Assert.assertEquals(yaml, message.getText());
            }
        }

    }

    /**
     * Tests that if the {@link AttackVector} is sent to the message end point, that it is not constructed.
     */
    @Test
    public void postMessageStringAttack() {
        final String yaml = "!!" +
                AttackVector.class.getName() +
                " {}";
        try (
                Response response = CLIENT.target(generateUri("/message"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should fail as the AttackVector should not be allowed
            checkExpectedStatus(response, Response.Status.BAD_REQUEST, Response.Status.INTERNAL_SERVER_ERROR);
            assertNotAttacked();
            assertEmpty();
        }
    }

    /**
     * Tests that a valid list of messages can be sent.
     */
    @Test
    public void postMessagesList() {
        final String yaml = "[!!" +
                Message.class.getName() +
                " {text: \"passed list\"}]";
        try (
                Response response = CLIENT.target(generateUri("/message/list"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should have an CREATED response
            Assert.assertEquals(Response.Status.CREATED, response.getStatusInfo());
            // We should have a location in the headers
            final URI location = response.getLocation();
            Assert.assertNotNull("Expected a Location header in " + response.getHeaders(), location);

            // Check the response link, it should be the yaml
            try (Response getResponse = CLIENT.target(location).request("text/yaml").get()) {
                // We should have an OK response
                Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
                final Map<String, Message> messages = getResponse.readEntity(new GenericType<Map<String, Message>>() {
                });
                Assert.assertNotNull("Messages should not be null", messages);
                Assert.assertEquals("Messages should only have one entry", 1, messages.size());
                Assert.assertEquals("passed list", messages.values().iterator().next().getText());
            }
        }
    }

    /**
     * Sends a valid message, and the attack. The {@link AttackVector} should not be constructed and the request should
     * fail.
     */
    @Test
    public void postMessagesListAttack() {
        final String yaml = "[!!" +
                Message.class.getName() +
                " {text: \"passed list with attack\"}, !!" +
                AttackVector.class.getName() + " {}]";
        try (
                Response response = CLIENT.target(generateUri("/message/list"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should fail as the AttackVector should not be allowed
            checkExpectedStatus(response, Response.Status.BAD_REQUEST, Response.Status.INTERNAL_SERVER_ERROR);
            assertNotAttacked();
            assertEmpty();
        }
    }

    /**
     * Tests that a valid set of messages can be sent.
     */
    @Test
    public void postMessagesSet() {
        final String yaml = "!!set {!!" +
                Message.class.getName() +
                " {text: \"passed set\"}}";
        try (
                Response response = CLIENT.target(generateUri("/message/set"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should have an CREATED response
            Assert.assertEquals(Response.Status.CREATED, response.getStatusInfo());
            // We should have a location in the headers
            final URI location = response.getLocation();
            Assert.assertNotNull("Expected a Location header in " + response.getHeaders(), location);

            // Check the response link, it should be the yaml
            try (Response getResponse = CLIENT.target(location).request("text/yaml").get()) {
                // We should have an OK response
                Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
                final Map<String, Message> messages = getResponse.readEntity(new GenericType<Map<String, Message>>() {
                });
                Assert.assertNotNull("Messages should not be null", messages);
                Assert.assertEquals("Messages should only have one entry", 1, messages.size());
                Assert.assertEquals("passed set", messages.values().iterator().next().getText());
            }
        }
    }

    /**
     * Sends a valid message, and the attack. The {@link AttackVector} should not be constructed and the request should
     * fail.
     */
    @Test
    public void postMessagesSetAttack() {
        final String yaml = "!!set {!!" +
                Message.class.getName() +
                " {text: \"passed set\"}, !!" +
                AttackVector.class.getName() + " {}}";
        try (
                Response response = CLIENT.target(generateUri("/message/set"))
                        .request("text/yaml")
                        .post(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should fail as the AttackVector should not be allowed
            checkExpectedStatus(response, Response.Status.BAD_REQUEST, Response.Status.INTERNAL_SERVER_ERROR);
            assertNotAttacked();
            assertEmpty();
        }
    }

    /**
     * Tests that a valid map of messages can be sent.
     */
    @Test
    public void putMessages() {
        final String id = UUID.randomUUID().toString();
        final String yaml = id + ": !!" +
                Message.class.getName() +
                " {text: \"passed map\"}";
        try (
                Response response = CLIENT.target(generateUri("/message/all"))
                        .request("text/yaml")
                        .put(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should have an CREATED response
            Assert.assertEquals(Response.Status.CREATED, response.getStatusInfo());
            // We should have a location in the headers
            final URI location = response.getLocation();
            Assert.assertNotNull("Expected a Location header in " + response.getHeaders(), location);

            // Check the response link, it should be the yaml
            try (Response getResponse = CLIENT.target(location).request("text/yaml").get()) {
                // We should have an OK response
                Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
                final Map<String, Message> messages = getResponse.readEntity(new GenericType<Map<String, Message>>() {
                });
                Assert.assertNotNull("Messages should not be null", messages);
                Assert.assertEquals("Messages should only have one entry", 1, messages.size());
                final Message message = messages.get(id);
                Assert.assertNotNull("Message not found with id " + id, message);
                Assert.assertEquals("passed map", message.getText());
            }
        }
    }

    /**
     * Sends a valid message, and the attack. The {@link AttackVector} should not be constructed and the request should
     * fail.
     */
    @Test
    public void putMessagesAttack() {
        final String id = UUID.randomUUID().toString();
        final String yaml = "[" + id + ": !!" +
                Message.class.getName() +
                " {text: \"passed map\"}, invalid: !!" +
                AttackVector.class.getName() + " {}]";
        try (
                Response response = CLIENT.target(generateUri("/message/all"))
                        .request("text/yaml")
                        .put(Entity.entity(yaml, "text/yaml"))
        ) {
            // We should fail as the AttackVector should not be allowed
            checkExpectedStatus(response, Response.Status.BAD_REQUEST, Response.Status.INTERNAL_SERVER_ERROR);
            assertNotAttacked();
            assertEmpty();
        }
    }

    private void checkExpectedStatus(final Response response, final Response.Status... allowedStatuses) {
        boolean ok = false;
        for (Response.Status status : allowedStatuses) {
            if (status == response.getStatusInfo()) {
                ok = true;
                break;
            }
        }
        Assert.assertTrue(String.format("Status %s was not expected; %s", response.getStatusInfo(), Arrays.toString(allowedStatuses)), ok);
    }

    private void assertNotAttacked() {
        // Check the attacked vector, it should be false
        try (Response getResponse = CLIENT.target(generateUri("/message/check/attacked")).request().get()) {
            // We should have an OK response
            Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
            Assert.assertFalse("The AttackVector was attacked!!", getResponse.readEntity(boolean.class));
        }
        // Check the attacked vector, it should be false
        try (Response getResponse = CLIENT.target(generateUri("/message/check/static/attacked")).request().get()) {
            // We should have an OK response
            Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
            Assert.assertFalse("The AttackVector was attacked via static initialization!!", getResponse.readEntity(boolean.class));
        }
    }

    private void assertEmpty() {
        // Check the attacked vector, it should be false
        try (Response getResponse = CLIENT.target(generateUri("/message/")).request().get()) {
            // We should have an OK response
            Assert.assertEquals(Response.Status.OK, getResponse.getStatusInfo());
            final Map<String, Message> messages = getResponse.readEntity(new GenericType<Map<String, Message>>() {
            });
            Assert.assertTrue("Expected empty messages, but got: " + messages, messages.isEmpty());
        }
    }

    private URI generateUri(final String path) {
        return UriBuilder.fromUri(uri).path(path).build();
    }

}
