package org.jboss.resteasy.test.asynch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Future;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletApp;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletAsyncResponseBlockingQueue;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletJaxrsResource;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletPrintingErrorHandler;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletResource;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletServiceUnavailableExceptionMapper;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletTimeoutHandler;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletXmlData;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for asyncHttpServlet module. Check stage URL
 *                    property.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ComprehensiveJaxrsTest
{
   protected static final Logger logger = LogManager.getLogger(ComprehensiveJaxrsTest.class.getName());

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, AsyncServletTest.class.getSimpleName() + ".war");
      war.addClasses(JaxrsAsyncServletXmlData.class, JaxrsAsyncServletAsyncResponseBlockingQueue.class,
            JaxrsAsyncServletJaxrsResource.class, JaxrsAsyncServletApp.class,
            JaxrsAsyncServletPrintingErrorHandler.class, JaxrsAsyncServletTimeoutHandler.class,
            JaxrsAsyncServletResource.class, JaxrsAsyncServletServiceUnavailableExceptionMapper.class,
            JaxrsAsyncServletXmlData.class);
      war.addAsWebInfResource(AsyncPostProcessingTest.class.getPackage(), "JaxrsAsyncServletWeb.xml", "web.xml");
      return war;
   }

   private String generateURL(String path)
   {
      return PortProviderUtil.generateURL(path, AsyncServletTest.class.getSimpleName());
   }

   protected Client client;

   @Before
   public void beforeTest()
   {
      client = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(10).build();
   }

   @After
   public void afterTest()
   {
      client.close();
   }

   protected static String objectsToString(Object... objects)
   {
      StringBuilder sb = new StringBuilder();
      for (Object o : objects)
      {
         sb.append(o).append(" ");
      }
      return sb.toString().trim();
   }

   public static void logMsg(Object... msg)
   {
      logger.info(objectsToString(msg));
   }

   protected static void checkEquals(Object expected, Object actual, Object... msg)
   {
      Assert.assertEquals(objectsToString(msg), expected, actual);
   }

   public static final TimeZone findTimeZoneInDate(String date)
   {
      StringBuilder sb = new StringBuilder();
      StringBuilder dateBuilder = new StringBuilder(date.trim()).reverse();
      int index = 0;
      char c;
      while ((c = dateBuilder.charAt(index++)) != ' ')
      {
         sb.append(c);
      }
      TimeZone timezone = TimeZone.getTimeZone(sb.reverse().toString());
      return timezone;
   }

   public static final DateFormat createDateFormat(TimeZone timezone)
   {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      sdf.setTimeZone(timezone);
      return sdf;
   }

   private void suspendResumeTestInternal() throws Exception
   {
      invokeClear();
      String expectedResponse = "Expected response";
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resume?stage=0", expectedResponse);
      checkString(resume, JaxrsAsyncServletResource.TRUE);
      checkString(suspend, expectedResponse);
   }

   private void cancelVoidTestInternal() throws Exception
   {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("cancelvoid?stage=0");
      checktStatus(getResponse(suspend), Status.SERVICE_UNAVAILABLE);
      checkString(cancel, JaxrsAsyncServletResource.TRUE);
   }

   private void setTimeoutTestInternal() throws Exception
   {
      invokeClear();
      logMsg("here 1");
      Future<Response> suspend = invokeRequest("suspend");
      logMsg("here 2");
      Future<Response> setTimeout = invokeRequest("settimeout?stage=0", 200);
      logMsg("here 3");
      checktStatus(getResponse(setTimeout), Status.NO_CONTENT);
      logMsg("here 4");
      // WebApplication exception with 503 is caught by
      // ServiceUnavailableExceptionMapper
      Response fromMapper = getResponse(suspend);
      logMsg("here 5");
      checktStatus(fromMapper, Status.REQUEST_TIMEOUT);
      String entity = fromMapper.readEntity(String.class);
      checkContains(entity, 503);
      logMsg("Found expected status 503");
   }

   private void cancelDateTestInternal() throws Exception
   {
      long milis = (System.currentTimeMillis() / 1000) * 1000 + 20000;
      invokeClear();

      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("canceldate?stage=0", milis);
      Response response = getResponse(suspend);
      checktStatus(response, Status.SERVICE_UNAVAILABLE);
      checkString(cancel, JaxrsAsyncServletResource.TRUE);
      String header = response.getHeaderString(HttpHeaders.RETRY_AFTER);
      TimeZone timezone = findTimeZoneInDate(header);
      Date retry = null;
      try
      {
         retry = createDateFormat(timezone).parse(header);
      }
      catch (ParseException e)
      {
         throw new Exception(e);
      }
      checkEquals(new Date(milis), retry, "Unexpected", HttpHeaders.RETRY_AFTER, "header value received",
            retry.getTime(), "expected", milis);
      logMsg("Found expected", HttpHeaders.RETRY_AFTER, "=", header);
   }

   private void cancelIntTestInternal() throws Exception
   {
      String seconds = "20";
      invokeClear();

      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("cancelretry?stage=0", seconds);
      Response response = getResponse(suspend);
      checktStatus(response, Status.SERVICE_UNAVAILABLE);
      checkString(cancel, JaxrsAsyncServletResource.TRUE);
      String retry = response.getHeaderString(HttpHeaders.RETRY_AFTER);
      checkEquals(seconds, retry, "Unexpected", HttpHeaders.RETRY_AFTER, "header value received", retry, "expected",
            seconds);
      logMsg("Found expected", HttpHeaders.RETRY_AFTER, "=", retry);
   }

   /**
    * @tpTestDetails Complex test. Check stage=0 and stage=1 values.
    * @tpSince RESTEasy 3.0.16
    */

   @Test
   public void cancelVoidTest() throws Exception
   {
      cancelVoidTestInternal();
   }

   @Test
   public void cancelVoidOnResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> cancel = invokeRequest("cancelvoid?stage=1");
      checkString(cancel, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void cancelVoidOnCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> cancel = invokeRequest("cancelvoid?stage=1");
      checkString(cancel, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void resumeCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> resumeCanceled = invokeRequest("resume?stage=1", "");
      checkString(resumeCanceled, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void cancelIntTest() throws Exception
   {
      cancelIntTestInternal();
   }

   @Test
   public void cancelIntOnResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> cancel = invokeRequest("cancelretry?stage=1", "20");
      checkString(cancel, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void cancelIntOnCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> cancel = invokeRequest("cancelretry?stage=1", "20");
      checkString(cancel, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void resumeCanceledIntTest() throws Exception
   {
      cancelIntTestInternal();
      Future<Response> resume = invokeRequest("resume?stage=1", "");
      checkString(resume, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void cancelDateTest() throws Exception
   {
      cancelDateTestInternal();
   }

   @Test
   public void cancelDateOnResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> cancel = invokeRequest("canceldate?stage=1", System.currentTimeMillis());
      checkString(cancel, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void cancelDateOnCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> cancel = invokeRequest("canceldate?stage=1", System.currentTimeMillis());
      checkString(cancel, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void resumeCanceledDateTest() throws Exception
   {
      cancelDateTestInternal();
      Future<Response> resumeResumed = invokeRequest("resume?stage=1", "");
      checkString(resumeResumed, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void isCanceledWhenCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> is = invokeRequest("iscanceled?stage=1");
      checkString(is, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void isCanceledWhenSuspendedTest() throws Exception
   {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("iscanceled?stage=0");
      checkString(is, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void isCanceledWhenResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> is = invokeRequest("iscanceled?stage=1");
      checkString(is, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void isDoneWhenResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> is = invokeRequest("isdone?stage=1");
      checkString(is, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void isDoneWhenSuspendedTest() throws Exception
   {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("isdone?stage=0");
      checkString(is, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void isDoneWhenCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> is = invokeRequest("isdone?stage=1");
      checkString(is, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void isDoneWhenTimedOutTest() throws Exception
   {
      setTimeoutTestInternal();
      Future<Response> is = invokeRequest("isdone?stage=1");
      checkString(is, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void isSuspendedWhenSuspendedTest() throws Exception
   {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("issuspended?stage=0");
      checkString(is, JaxrsAsyncServletResource.TRUE);
   }

   @Test
   public void isSuspendedWhenCanceledTest() throws Exception
   {
      cancelVoidTestInternal();
      Future<Response> is = invokeRequest("issuspended?stage=1");
      checkString(is, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void isSuspendedWhenResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> is = invokeRequest("issuspended?stage=1");
      checkString(is, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void suspendResumeTest() throws Exception
   {
      suspendResumeTestInternal();
   }

   @Test
   public void resumeAnyJavaObjectInputStreamTest() throws Exception
   {
      invokeClear();
      String expectedResponse = "Expected response";
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resume?stage=0", new ByteArrayInputStream(expectedResponse.getBytes()));
      checkString(resume, JaxrsAsyncServletResource.TRUE);
      checkString(suspend, expectedResponse);
   }

   @Test
   public void resumeResumedTest() throws Exception
   {
      suspendResumeTestInternal(); // resume & store
      Future<Response> resumeResumed = invokeRequest("resume?stage=1", "");
      checkString(resumeResumed, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void resumeWithCheckedExceptionTest() throws Exception
   {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resumechecked?stage=0");
      checkString(resume, JaxrsAsyncServletResource.TRUE);
      checkException(suspend, IOException.class);
   }

   @Test
   public void resumeWithRuntimeExceptionTest() throws Exception
   {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resumeruntime?stage=0");
      checkString(resume, JaxrsAsyncServletResource.TRUE);
      checkException(suspend, RuntimeException.class);
   }

   @Test
   public void resumeWithExceptionReturnsFalseWhenResumedTest() throws Exception
   {
      suspendResumeTestInternal();
      Future<Response> resume = invokeRequest("resumechecked?stage=1");
      checkString(resume, JaxrsAsyncServletResource.FALSE);
   }

   @Test
   public void setTimeoutTest() throws Exception
   {
      setTimeoutTestInternal();
   }

   @Test
   public void updateTimeoutTest() throws Exception
   {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("settimeout?stage=0", 600000);
      checktStatus(getResponse(setTimeout), Status.NO_CONTENT);
      checkFalse(suspend.isDone(), "Suspended AsyncResponse already received");
      setTimeout = invokeRequest("settimeout?stage=1", 200);
      checktStatus(getResponse(setTimeout), Status.NO_CONTENT);
      // WebApplication exception with 503 is caught by
      // ServiceUnavailableExceptionMapper
      Response fromMapper = getResponse(suspend);
      checktStatus(fromMapper, Status.REQUEST_TIMEOUT);
      String entity = fromMapper.readEntity(String.class);
      checkContains(entity, HttpResponseCodes.SC_SERVICE_UNAVAILABLE);
      logMsg("Found expected status 503");
   }

   @Test
   public void handleTimeOutWaitsForeverTest() throws Exception
   {
      String responseMsg = "handleTimeOutWaitsForeverTest";
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("timeouthandler?stage=0", 1);
      Future<Response> resume = invokeRequest("resume?stage=1", responseMsg);
      checktStatus(getResponse(setTimeout), Status.NO_CONTENT);
      checkString(resume, JaxrsAsyncServletResource.TRUE);
      checkString(suspend, responseMsg);
   }

   @Test
   public void handleTimeoutCancelsTest() throws Exception
   {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("timeouthandler?stage=0", 2);
      checktStatus(getResponse(setTimeout), Status.NO_CONTENT);
      checktStatus(getResponse(suspend), Status.SERVICE_UNAVAILABLE);
      Future<Response> resume = invokeRequest("issuspended?stage=1");
      checkString(resume, JaxrsAsyncServletResource.FALSE);

   }

   @Test
   public void handleTimeoutResumesTest() throws Exception
   {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("timeouthandler?stage=0", 3);
      checktStatus(getResponse(setTimeout), Status.NO_CONTENT);
      checkString(suspend, JaxrsAsyncServletResource.RESUMED);
      Future<Response> resume = invokeRequest("issuspended?stage=1");
      checkString(resume, JaxrsAsyncServletResource.FALSE);
   }
   // }

   protected String getAbsoluteUrl()
   {
      return generateURL("/resource");
   }

   private void invokeClear() throws Exception
   {
      Response response = client.target(getAbsoluteUrl()).path("clear").request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
   }

   private Future<Response> invokeRequest(String resource)
   {
      AsyncInvoker async = createAsyncInvoker(resource);
      Future<Response> future = async.get();
      return future;
   }

   private <T> Future<Response> invokeRequest(String resource, T entity)
   {
      AsyncInvoker async = createAsyncInvoker(resource);
      Future<Response> future = async.post(Entity.entity(entity, MediaType.TEXT_PLAIN_TYPE));
      return future;
   }

   private AsyncInvoker createAsyncInvoker(String resource)
   {
      WebTarget target = client.target(generateURL("/resource/" + resource));
      AsyncInvoker async = target.request().async();
      return async;
   }

   private static Response getResponse(Future<Response> future) throws Exception
   {
      Response response = future.get();
      return response;
   }

   private static void checktStatus(Response response, Response.Status status) throws Exception
   {
      checkEquals(response.getStatus(), status.getStatusCode(), "Unexpected status code received", response.getStatus(),
            "expected was", status);
      logMsg("Found expected status", status);
   }

   private static void checkString(Future<Response> future, String check) throws Exception
   {
      Response response = getResponse(future);
      checktStatus(response, Status.OK);
      String content = response.readEntity(String.class);
      checkEquals(check, content, "Unexpected response content", content);
      logMsg("Found expected string", check);
   }

   private static void checkException(Future<Response> future, Class<? extends Throwable> e) throws Exception
   {
      String clazz = e.getName();
      Response response = getResponse(future);
      checktStatus(response, Response.Status.NOT_ACCEPTABLE);
      checkContainsString(response.readEntity(String.class), clazz, clazz, "not thrown");
      logMsg(clazz, "has been thrown as expected");
   }

   public static void checkContainsString(String string, String substring, Object... message) throws Exception
   {
      checkTrue(string.contains(substring), message);
   }

   public static <T> void checkContains(T text, T subtext, Object... message) throws Exception
   {
      checkContainsString(text.toString(), subtext.toString(), message);
   }

   public static void checkTrue(boolean condition, Object... message)
   {
      if (!condition)
      {
         Assert.fail(objectsToString(message));
      }
   }

   public static void checkFalse(boolean condition, Object... message) throws Exception
   {
      checkTrue(!condition, message);
   }

}
