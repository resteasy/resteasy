package org.jboss.resteasy.test.client.proxy;

import static org.jboss.resteasy.test.client.proxy.resource.GenericEntities.GenericEntityExtendingBaseEntityResource.FIRST_NAME;
import static org.jboss.resteasy.test.client.proxy.resource.GenericEntities.GenericEntityExtendingBaseEntityResource.LAST_NAME;

import java.util.HashMap;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.proxy.resource.GenericEntities.EntityExtendingBaseEntity;
import org.jboss.resteasy.test.client.proxy.resource.GenericEntities.GenericEntityExtendingBaseEntityProxy;
import org.jboss.resteasy.test.client.proxy.resource.GenericEntities.GenericEntityExtendingBaseEntityResource;
import org.jboss.resteasy.test.client.proxy.resource.GenericEntities.MultipleGenericEntities;
import org.jboss.resteasy.test.client.proxy.resource.GenericEntities.MultipleGenericEntitiesProxy;
import org.jboss.resteasy.test.client.proxy.resource.GenericEntities.MultipleGenericEntitiesResource;
import org.jboss.resteasy.test.client.proxy.resource.GenericProxyBase;
import org.jboss.resteasy.test.client.proxy.resource.GenericProxyResource;
import org.jboss.resteasy.test.client.proxy.resource.GenericProxySpecificProxy;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1047.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GenericProxyTest {
    private static ResteasyClient client;

    @BeforeAll
    public static void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GenericProxyTest.class.getSimpleName());
        war.addClasses(GenericProxyBase.class, GenericProxySpecificProxy.class);
        war.addPackage(MultipleGenericEntities.class.getPackage());
        return TestUtil.finishContainerPrepare(war, null, GenericProxyResource.class,
                GenericEntityExtendingBaseEntityResource.class,
                MultipleGenericEntitiesResource.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(GenericProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test generic proxy in client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEcho() {
        ResteasyWebTarget target = client.target(generateBaseUrl());
        GenericProxySpecificProxy proxy = target.proxy(GenericProxySpecificProxy.class);
        String hello = proxy.sayHi("hello");
        Assertions.assertEquals("hello", hello, "Response has wrong content");
        hello = proxy.sayHi("hello123");
        Assertions.assertEquals("hello123", hello, "Response has wrong content");
    }

    /**
     * @tpTestDetails Test generic proxy in client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEchoNoProxy() {
        ResteasyWebTarget target = client.target(generateBaseUrl() + "/say/hello");
        Response response = target.request().post(Entity.text("hello"));

        String hello = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", hello, "Response has wrong content");

        response.close();
    }

    /**
     * @tpTestDetails Test generic proxy in client extending another interface. Test for RESTEASY-1432.
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testInterfaceWithGenericTypeWithClientProxy() {
        GenericEntityExtendingBaseEntityProxy proxy = client.target(generateBaseUrl())
                .proxy(GenericEntityExtendingBaseEntityProxy.class);
        EntityExtendingBaseEntity entity;

        entity = proxy.findOne();
        Assertions.assertEquals(entity.getLastName(), LAST_NAME);

        List<EntityExtendingBaseEntity> entities = proxy.findAll();
        Assertions.assertEquals(entities.get(0).getLastName(), LAST_NAME);

        MultipleGenericEntitiesProxy proxy1 = client.target(generateBaseUrl()).proxy(MultipleGenericEntitiesProxy.class);

        HashMap<String, EntityExtendingBaseEntity> hashMap = proxy1.findHashMap();
        entity = hashMap.get(FIRST_NAME);
        Assertions.assertEquals(entity.getLastName(), LAST_NAME);
    }

}
