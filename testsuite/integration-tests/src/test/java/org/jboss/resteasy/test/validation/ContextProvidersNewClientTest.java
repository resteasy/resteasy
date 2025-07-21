package org.jboss.resteasy.test.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomer;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomerForm;
import org.jboss.resteasy.test.validation.resource.ContextProvidersName;
import org.jboss.resteasy.test.validation.resource.ContextProvidersResource;
import org.jboss.resteasy.test.validation.resource.ContextProvidersXop;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1119. Test for new client.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ContextProvidersNewClientTest extends ContextProvidersTestBase {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ContextProvidersNewClientTest.class.getSimpleName())
                .addClasses(ContextProvidersCustomer.class, ContextProvidersCustomerForm.class,
                        ContextProvidersName.class, ContextProvidersXop.class)
                .addClass(ContextProvidersTestBase.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, ContextProvidersResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContextProvidersNewClientTest.class.getSimpleName());
    }

    protected Client client;

    @BeforeEach
    public void beforeTest() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void afterTest() {
        client.close();
    }

    @Override
    <T> T get(String path, Class<T> clazz, Annotation[] annotations) throws Exception {
        WebTarget target = client.target(generateURL(path));
        ClientResponse response = (ClientResponse) target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        T entity = response.readEntity(clazz, null, annotations);
        return entity;
    }

    @Override
    <S, T> T post(String path, S payload, MediaType mediaType,
            Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception {
        WebTarget target = client.target(generateURL(path));
        Entity<S> entity = Entity.entity(payload, mediaType, annotations);
        ClientResponse response = (ClientResponse) target.request().post(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        T result;
        if (genericReturnType != null) {
            result = response.readEntity(returnType, genericReturnType, null);
        } else {
            result = response.readEntity(returnType);
        }
        return result;
    }

}
