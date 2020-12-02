package org.jboss.resteasy.test.mapper;

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
import org.junit.Assert;
import org.junit.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Test that the Exception processing and ExceptionMapper handling rules defined in
 * the jsr-311 section 3.3.4 are dealt with correctly.  Related information is provided
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

      Assert.assertEquals("SprocketDBExceptionMapper: incorrect status code returned",
              result.getStatus(), SprocketDBExceptionMapper.STATUS_CODE);
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
      Response result = eHandler.handleException(request,appE);

      Assert.assertEquals("First ApplicationException: incorrect status code returned",
              result.getStatus(), statusCode);


      // ApplicationException is a 'final' class and can not be subclasses.
      factory.registerProvider(ApplicationExceptionMapper.class);
      Response resultOne = eHandler.handleException(request,
              new ApplicationException("ApplicationException test", new SprocketDBException()));

      Assert.assertEquals("Second ApplicationException: incorrect status code returned",
              resultOne.getStatus(), ApplicationExceptionMapper.STATUS_CODE);
   }

   @Test
   public void testUnhandledException() throws Exception {

      ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
      HttpRequest request = MockHttpRequest.get("/locating/basic");

      ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);

      // Check that an exception unknown to Resteasy and with no ExceptionMapper
      // is flagged with an UnknownException.
      SprocketDBException sdbe = new SprocketDBException("SprocketDBException test",
              new ApplicationException("ApplicationException test",
                      new IOException("IOException child")));

      boolean isTestSuccess = false;
      try
      {
         Response result = eHandler.handleException(request, sdbe);
      } catch (UnhandledException ue) {
         isTestSuccess = true;
      }
      Assert.assertTrue("Test failed to properly throw UnhandledException", isTestSuccess);
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

      Assert.assertEquals("One WebApplicationException: incorrect status code returned",
              result.getStatus(), statusCode);

      // When WebApplicationException and response is null an or entity is null
      // use custom internal mapper to produce response.
      statusCode = 111333555;
      NoLogWebApplicationException nlwaeOne = new NoLogWebApplicationException(
              Response.status(statusCode).build());
      Response resultOne = eHandler.handleException(request, nlwaeOne);

      Assert.assertEquals("One WebApplicationException: incorrect status code returned",
              resultOne.getStatus(), statusCode);
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

      Assert.assertEquals("One LoggableFailure: incorrect status code returned",
              result.getStatus(), statusCode);
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

      Assert.assertEquals("IOException: incorrect status code returned",
              result.getStatus(), HttpResponseCodes.SC_NO_CONTENT);

   }
}