package org.jboss.resteasy.test.client;

import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.ext.Provider;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpSince RESTEasy 4.5.0
 * @tpTestCaseDetails Regression test for RESTEASY-2402
 *
 */
public class ClientConfigurationImmutableSetsTest {

    private static Client client;

    @BeforeAll
    public static void beforeClass() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void afterClass() {
        client.close();
    }

    public static class TestClass {
    }

    @Provider
    public static class TestProvider {
    };

    @Test
    public void getClassesIsImmutableTest() {
        Set<Class<?>> classes = client.getConfiguration().getClasses();
        int size = classes.size();
        try {
            classes.add(TestClass.class);
        } catch (Exception e) {
            // can throw exception or do nothing
            // when adding to this immutable set
            // or it can be a new hard copied set
        }
        Assertions.assertEquals(size, classes.size());
    }

    @Test
    public void getInstancesIsImmutableTest() {
        Set<Object> instances = client.getConfiguration().getInstances();
        int size = instances.size();
        try {
            instances.add(new TestProvider());
        } catch (Exception e) {
            // can throw exception or do nothing
            // when adding to this immutable set
            // or it can be a new hard copied set
        }
        Assertions.assertEquals(size, instances.size());
    }

    @Test
    public void getPropertiesIsImmutableTest() {
        Map<String, Object> properties = client.getConfiguration().getProperties();
        int size = properties.size();
        try {
            properties.put("p", "v");
        } catch (Exception e) {
            // can throw exception or do nothing
            // when adding to this immutable set
            // or it can be a new hard copied set
        }
        Assertions.assertEquals(size, properties.size());
    }
}
