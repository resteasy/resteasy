package org.jboss.resteasy.test.mapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.ExceptionHandler;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.UnhandledException;
import org.jboss.resteasy.test.mapper.resource.ApplicationExceptionMapper;
import org.jboss.resteasy.test.mapper.resource.IOExceptionMapper;
import org.jboss.resteasy.test.mapper.resource.SprocketDBException;
import org.jboss.resteasy.test.mapper.resource.SprocketDBExceptionMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that the Exception processing and ExceptionMapper handling rules defined in
 * the jsr-311 section 3.3.4 are dealt with correctly. Related information is provided
 * in the Resteasy guide section 29.
 *
 * @tpSubChapter Exception-Handling
 * @tpChapter Unit tests
 * @tpSince RESTEasy 4.0.0
 */
public class ExceptionHandlerTest {

    private Set<String> unwrappedExceptions = new HashSet<String>();

    @Test
    public void testExecuteExactExceptionMapper() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        factory.registerProvider(SprocketDBExceptionMapper.class);
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        // Check that the internal method that looks for a specific ExceptionMapper
        // by Exception classname finds it.
        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);
        Response result = eHandler.handleException(request,
                new SprocketDBException("SprocketDBException test"));

        Assertions.assertEquals(result.getStatus(), SprocketDBExceptionMapper.STATUS_CODE,
                "SprocketDBExceptionMapper: incorrect status code returned");
    }

    @Test
    public void testApplicationExceptionMapper() throws Exception {

        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);

        // When no ApplicationException mapper is provided and one of the (thrown) causes is
        // of type WebApplicationException and WebApplicationException instance provides
        // a response object that has an non-null entity, return that response.
        int statusCode = 111444777;
        WebApplicationException webAppE = new WebApplicationException(
                Response.status(statusCode).entity("WebApplicationException test").build());
        ApplicationException appE = new ApplicationException("ApplicationException test", webAppE);
        Response result = eHandler.handleException(request, appE);

        Assertions.assertEquals(result.getStatus(), statusCode,
                "First ApplicationException: incorrect status code returned");

        // ApplicationException is a 'final' class and can not be subclasses.
        factory.registerProvider(ApplicationExceptionMapper.class);
        Response resultOne = eHandler.handleException(request,
                new ApplicationException("ApplicationException test", new SprocketDBException()));

        Assertions.assertEquals(resultOne.getStatus(), ApplicationExceptionMapper.STATUS_CODE,
                "Second ApplicationException: incorrect status code returned");
    }

    @Test
    public void testDefaultExceptionMapper() throws Exception {

        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);

        // Check that an exception unknown to Resteasy is processed with the default exception mapper
        SprocketDBException sdbe = new SprocketDBException("SprocketDBException test",
                new ApplicationException("ApplicationException test",
                        new IOException("IOException child")));

        try {
            Response result = eHandler.handleException(request, sdbe);
            Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, result.getStatusInfo());
            Assertions.assertEquals("SprocketDBException test", result.readEntity(String.class));
        } catch (UnhandledException ue) {
            try (
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw)) {
                ue.printStackTrace(pw);
                Assertions.fail("An unexpected exception has occured: " + sw);
            }
        }
    }

    @Test
    public void testWebApplicationException() throws Exception {

        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        // When WebApplicationException and response is not null and entity is not null
        // return the response.
        int statusCode = 777888999;
        NoLogWebApplicationException nlwae = new NoLogWebApplicationException(
                Response.status(statusCode).entity("NoLogWebApplicationException test").build());
        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);
        Response result = eHandler.handleException(request, nlwae);

        Assertions.assertEquals(result.getStatus(), statusCode,
                "One WebApplicationException: incorrect status code returned");

        // When WebApplicationException and response is null an or entity is null
        // use custom internal mapper to produce response.
        statusCode = 111333555;
        NoLogWebApplicationException nlwaeOne = new NoLogWebApplicationException(
                Response.status(statusCode).build());
        Response resultOne = eHandler.handleException(request, nlwaeOne);

        Assertions.assertEquals(resultOne.getStatus(), statusCode,
                "One WebApplicationException: incorrect status code returned");
    }

    @Test
    public void testFailure() throws Exception {

        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        // When Failure exception if response is not null return response.
        int statusCode = 222444666;
        LoggableFailure lf = new LoggableFailure("LoggableFailure test",
                Response.status(statusCode).entity("LoggableFailure entity").build());
        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);
        Response result = eHandler.handleException(request, lf);

        Assertions.assertEquals(result.getStatus(), statusCode,
                "One LoggableFailure: incorrect status code returned");
    }

    @Test
    public void testFailureWithNoResponse() throws Exception {

        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);

        // Check that a failure without a response gets a default response of type "text/plain" instead of "text/html"
        // (RESTEASY-3500)
        LoggableFailure ApplicationFailure = new LoggableFailure("Random Failure message");

        try {
            Response result = eHandler.handleException(request, ApplicationFailure);
            Assertions.assertEquals("text/plain", result.getMetadata().get("Content-Type").get(0));
        } catch (UnhandledException ue) {
            try (
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw)) {
                ue.printStackTrace(pw);
                Assertions.fail("An unexpected exception has occured: " + sw);
            }
        }
    }

    @Test
    public void TestIOExceptionMapper() throws Exception {

        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        factory.registerProvider(IOExceptionMapper.class);
        HttpRequest request = MockHttpRequest.get("/locating/basic");

        // Case where not a spec defined exception type or a Resteasy unique exception and
        // no mapper is provided.  Spec states return a 204 status in the response.
        IOException ioe = new IOException();
        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);
        Response result = eHandler.handleException(request, ioe);

        Assertions.assertEquals(result.getStatus(), HttpResponseCodes.SC_NO_CONTENT,
                "IOException: incorrect status code returned");

    }
}
