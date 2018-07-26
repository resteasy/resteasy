package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomer;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomerForm;
import org.jboss.resteasy.test.validation.resource.ContextProvidersName;
import org.jboss.resteasy.test.validation.resource.ContextProvidersResource;
import org.jboss.resteasy.test.validation.resource.ContextProvidersXop;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1119. Test for new client.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContextProvidersNewClientTest extends ContextProvidersTestBase {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ContextProvidersNewClientTest.class.getSimpleName())
                .addClasses(ContextProvidersCustomer.class, ContextProvidersCustomerForm.class,
                        ContextProvidersName.class, ContextProvidersXop.class)
                .addClass(ContextProvidersTestBase.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ContextProvidersResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContextProvidersNewClientTest.class.getSimpleName());
    }

    @Override
    <T> T get(String path, Class<T> clazz, Annotation[] annotations) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL(path));
        ClientResponse response = (ClientResponse) target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        T entity = response.readEntity(clazz, null, annotations);
        return entity;
    }

    @Override
    <S, T> T post(String path, S payload, MediaType mediaType,
                  Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL(path));
        Entity<S> entity = Entity.entity(payload, mediaType, annotations);
        ClientResponse response = (ClientResponse) target.request().post(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        T result;
        if (genericReturnType != null) {
            result = response.readEntity(returnType, genericReturnType, null);
        } else {
            result = response.readEntity(returnType);
        }
        return result;
    }

}
