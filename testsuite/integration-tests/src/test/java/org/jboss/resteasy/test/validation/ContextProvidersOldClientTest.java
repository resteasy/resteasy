package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest; //@cs-: clientrequest (Test for old client)
import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Test for old client)
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomer;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomerForm;
import org.jboss.resteasy.test.validation.resource.ContextProvidersName;
import org.jboss.resteasy.test.validation.resource.ContextProvidersResource;
import org.jboss.resteasy.test.validation.resource.ContextProvidersXop;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1119. Test for old client.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContextProvidersOldClientTest extends ContextProvidersTestBase {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ContextProvidersOldClientTest.class.getSimpleName())
                .addClasses(ContextProvidersCustomer.class, ContextProvidersCustomerForm.class, ContextProvidersName.class, ContextProvidersXop.class)
                .addClass(ContextProvidersTestBase.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return TestUtil.finishContainerPrepare(war, null, ContextProvidersResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContextProvidersOldClientTest.class.getSimpleName());
    }

    @Override
    <T> T get(String path, Class<T> clazz, Annotation[] annotations) throws Exception {
        ClientRequest request = new ClientRequest(generateURL(path));
        ClientResponse<T> response = request.get(clazz);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        T entity = response.getEntity(clazz, null, annotations);
        return entity;
    }

    @Override
    <S, T> T post(String path, S payload, MediaType mediaType,
                  Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception {
        ClientRequest request = new ClientRequest(generateURL(path));
        request.body(mediaType, payload, payload.getClass(), null, annotations);
        ClientResponse response = request.post();
        T entity;
        if (genericReturnType != null) {
            entity = (T) response.getEntity(returnType, genericReturnType);
        } else {
            entity = (T) response.getEntity(returnType);
        }

        return entity;
    }

}
