package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature6;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter6;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader6;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import java.util.Set;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Regression test for RESTEASY-1345
 * @tpSince RESTEasy 3.0.17
 */
public class ConfigurationInheritenceTest extends ResteasyProviderFactory {
    private static ConfigurationInheritenceTestFeature2 testFeature2 = new ConfigurationInheritenceTestFeature2();
    private static ConfigurationInheritenceTestFeature4 testFeature4 = new ConfigurationInheritenceTestFeature4();
    private static ConfigurationInheritenceTestFeature6 testFeature6 = new ConfigurationInheritenceTestFeature6();
    private static ConfigurationInheritenceTestFilter2 testFilter2 = new ConfigurationInheritenceTestFilter2();
    private static ConfigurationInheritenceTestFilter4 testFilter4 = new ConfigurationInheritenceTestFilter4();
    private static ConfigurationInheritenceTestFilter6 testFilter6 = new ConfigurationInheritenceTestFilter6();
    private static ConfigurationInheritenceTestMessageBodyReader2 testMessageBodyReader2 = new ConfigurationInheritenceTestMessageBodyReader2();
    private static ConfigurationInheritenceTestMessageBodyReader4 testMessageBodyReader4 = new ConfigurationInheritenceTestMessageBodyReader4();
    private static ConfigurationInheritenceTestMessageBodyReader6 testMessageBodyReader6 = new ConfigurationInheritenceTestMessageBodyReader6();

    private static final String ERROR_MSG = "Error during client-side registration";

    /**
     * @tpTestDetails Register items to clientBuilder.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testClientBuilderToClient() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder.register(ConfigurationInheritenceTestFeature1.class);
        clientBuilder.register(testFeature2);
        clientBuilder.register(new ConfigurationInheritenceTestFilter1());
        clientBuilder.register(testFilter2);
        clientBuilder.register(new ConfigurationInheritenceTestMessageBodyReader1());
        clientBuilder.register(testMessageBodyReader2);
        clientBuilder.property("property1", "value1");

        Client client = clientBuilder.build();
        client.register(ConfigurationInheritenceTestFeature3.class);
        client.register(testFeature4);
        client.register(new ConfigurationInheritenceTestFilter3());
        client.register(testFilter4);
        client.register(new ConfigurationInheritenceTestMessageBodyReader3());
        client.register(testMessageBodyReader4);
        client.property("property2", "value2");

        clientBuilder.register(ConfigurationInheritenceTestFeature5.class);
        clientBuilder.register(testFeature6);
        clientBuilder.register(new ConfigurationInheritenceTestFilter5());
        clientBuilder.register(testFilter6);
        clientBuilder.register(new ConfigurationInheritenceTestMessageBodyReader5());
        clientBuilder.register(testMessageBodyReader6);
        clientBuilder.property("property3", "value3");

        checkFirstConfiguration(clientBuilder.getConfiguration());
        checkSecondConfiguration(client.getConfiguration());
    }

    /**
     * @tpTestDetails Register items to client.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testClientToWebTarget() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        Client client = clientBuilder.build();
        client.register(ConfigurationInheritenceTestFeature1.class);
        client.register(testFeature2);
        client.register(new ConfigurationInheritenceTestFilter1());
        client.register(testFilter2);
        client.register(new ConfigurationInheritenceTestMessageBodyReader1());
        client.register(testMessageBodyReader2);
        client.property("property1", "value1");

        WebTarget target = client.target("http://localhost:8081");
        target.register(ConfigurationInheritenceTestFeature3.class);
        target.register(testFeature4);
        target.register(new ConfigurationInheritenceTestFilter3());
        target.register(testFilter4);
        target.register(new ConfigurationInheritenceTestMessageBodyReader3());
        target.register(testMessageBodyReader4);
        target.property("property2", "value2");

        client.register(ConfigurationInheritenceTestFeature5.class);
        client.register(testFeature6);
        client.register(new ConfigurationInheritenceTestFilter5());
        client.register(testFilter6);
        client.register(new ConfigurationInheritenceTestMessageBodyReader5());
        client.register(testMessageBodyReader6);
        client.property("property3", "value3");

        checkFirstConfiguration(client.getConfiguration());
        checkSecondConfiguration(target.getConfiguration());
    }

    /**
     * @tpTestDetails Check default RuntimeType oc clientBuilder, client end webTarget.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testRuntimeType() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        Assert.assertEquals("Wrong RuntimeType in ClientBuilder", RuntimeType.CLIENT, clientBuilder.getConfiguration().getRuntimeType());
        Client client = clientBuilder.build();
        Assert.assertEquals("Wrong RuntimeType in Client", RuntimeType.CLIENT, client.getConfiguration().getRuntimeType());
        WebTarget target = client.target("http://localhost:8081");
        Assert.assertEquals("Wrong RuntimeType in WebTarget", RuntimeType.CLIENT, target.getConfiguration().getRuntimeType());
    }

    private void checkFirstConfiguration(Configuration config) {
        Set<Class<?>> classes = config.getClasses();
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature1.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature3.class));
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature5.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFilter3.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFilter4.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestMessageBodyReader3.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestMessageBodyReader4.class));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature2.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature3.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature4.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature5.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature6.class));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature2.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature3.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature4.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature5.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter2.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter3.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter4.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter5.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader2.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader3.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader4.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader5.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader6.class));

        Set<Object> instances = config.getInstances();
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature2));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFeature4));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter2));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFilter4));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader2));
        Assert.assertFalse(ERROR_MSG, instances.contains(testMessageBodyReader4));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader6));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature2));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(testFeature4));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature6));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature2));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFeature4));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter2));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFilter4));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader2));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testMessageBodyReader4));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader6));

        Assert.assertEquals(ERROR_MSG, 2, config.getProperties().size());
        Assert.assertEquals(ERROR_MSG, "value1", config.getProperty("property1"));
        Assert.assertEquals(ERROR_MSG, "value3", config.getProperty("property3"));

        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature1.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature2.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature3.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature4.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature5.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature6.class).isEmpty());
    }

    private void checkSecondConfiguration(Configuration config) {
        Set<Class<?>> classes = config.getClasses();
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature3.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature5.class));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature2.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature3.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature4.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature5.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature6.class));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature2.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature3.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature4.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature5.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter2.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter3.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter4.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter5.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader2.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader3.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader4.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader5.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader6.class));

        Set<Object> instances = config.getInstances();
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature2));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature4));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFeature6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter2));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter4));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFilter6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader2));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader4));
        Assert.assertFalse(ERROR_MSG, instances.contains(testMessageBodyReader6));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature2));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature4));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(testFeature6));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature2));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature4));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFeature6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter2));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter4));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFilter6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader2));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader4));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testMessageBodyReader6));

        Assert.assertEquals(ERROR_MSG, 2, config.getProperties().size());
        Assert.assertEquals(ERROR_MSG, "value1", config.getProperty("property1"));
        Assert.assertEquals(ERROR_MSG, "value2", config.getProperty("property2"));

        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature1.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature2.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature3.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature4.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature5.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature6.class).isEmpty());
    }
}