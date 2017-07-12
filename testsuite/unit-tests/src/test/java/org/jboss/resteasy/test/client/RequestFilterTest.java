package org.jboss.resteasy.test.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.test.client.resource.ClientCustomException;
import org.jboss.resteasy.test.client.resource.RequestFilterAbortWith;
import org.jboss.resteasy.test.client.resource.RequestFilterAcceptLanguage;
import org.jboss.resteasy.test.client.resource.RequestFilterAnnotation;
import org.jboss.resteasy.test.client.resource.RequestFilterGetEntity;
import org.jboss.resteasy.test.client.resource.RequestFilterSetEntity;
import org.jboss.resteasy.test.client.resource.RequestFilterThrowCustomException;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.util.Locale;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
public class RequestFilterTest {

    protected static final Logger logger = LogManager.getLogger(RequestFilterTest.class.getName());

    static Client client;
    String dummyUrl = "dummyUrl";

    @BeforeClass
    public static void setupClient() {
        client = ClientBuilder.newClient();

    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client registers implementation of ClientRequestFilter and sends GET request. The request is
     * processed by registered filter before sending it to the server. Filter aborts processing and sends its own
     * response
     * @tpPassCrit Expected String is returned from the ClientRequestFilter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AbortWithTest() {
        Response response = client.target(dummyUrl).register(RequestFilterAbortWith.class).request().get();
        String str = response.readEntity(String.class);
        Assert.assertEquals("String returned from ClientRequest filter doesn't contain the expected value", "42", str);
    }

    /**
     * @tpTestDetails Client registers implementation of ClientRequestFilter and sends GET request.
     * The request has preset accepted language types. The request is processed by registered filter before
     * it is send to the server. Filter aborts processing and sends its own response with list of acceptable languages
     * from the request.
     * @tpPassCrit Expected String is returned from the ClientRequestFilter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AcceptLanguagesTest() {
        Response response = client.target(dummyUrl).register(RequestFilterAcceptLanguage.class).request()
                .acceptLanguage(Locale.CANADA_FRENCH)
                .acceptLanguage(Locale.PRC).get();
        String str = response.readEntity(String.class);
        logger.info("The locale from the response: " + str);
        logger.info("The expected locale: "  + Locale.CANADA_FRENCH.toString());
        Assert.assertTrue("String returned from ClientRequest filter doesn't contain accepted locale",
                str.contains(Locale.CANADA_FRENCH.toString()));

    }

    /**
     * @tpTestDetails Client registers implementations of ClientRequestFilters and sends POST request.
     * The request is processed by both filters one setting request entity and the other checking the entity is the same
     * entity set up in the first filter. Second Filter aborts processing and sends its own response.
     * from the request.
     * @tpPassCrit The request is processed by both filters.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void MultipleFiltersTest() {
        Entity<ByteArrayInputStream> entity = Entity.entity(new ByteArrayInputStream(
                "test".getBytes()), MediaType.WILDCARD_TYPE);
        Response response = client.target(dummyUrl).register(RequestFilterSetEntity.class)
                .register(RequestFilterGetEntity.class).request().post(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }

    /**
     * @tpTestDetails Client registers implementation of ClientRequestFilter and sends POST request.
     * The request is processed by registered filter before it is send to the server. Filter aborts processing
     * and sends its own response with request entity annotation.
     * @tpPassCrit Expected Annotation is returned from the ClientRequestFilter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AnnotationFilterTest() {
        Entity<String> post = Entity.entity("test", MediaType.WILDCARD_TYPE,
                RequestFilterAnnotation.class.getAnnotations());
        Response response = client.target(dummyUrl).register(RequestFilterAnnotation.class).request().post(post);
        Assert.assertEquals("The response doesn't contain the expexted provider name",
                Provider.class.getName(), response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client registers implementation of ClientRequestFilter and sends GET request.
     * The request is processed by registered filter before it is send to the server. Filter aborts processing
     * by throwing a custom exception, which should not be wrapped in a ProcessingException. [RESTEASY-1591]
     * @tpPassCrit Expected Exception is thrown from the Client
     * @tpSince RESTEasy 3.0.21
     */
    @Test
    public void ThrowCustomExceptionFilterTest() {
    	try {
    		client.target(dummyUrl).register(RequestFilterThrowCustomException.class).request().get();
    		Assert.fail();
    	} catch (ProcessingException pe) {
    		Assert.assertEquals(ClientCustomException.class, pe.getCause().getClass());
    	}
    }
}
