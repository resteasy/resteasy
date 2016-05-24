package org.jboss.resteasy.test.async;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ComprehensiveJaxrsTest
{
   protected Client client;

   @Before
   public void beforeTest() {
      client = new ResteasyClientBuilder().connectionPoolSize(10).build();
   }

   @After
   public void afterTest() {
      client.close();
   }

   protected static String objectsToString(Object... objects) {
      StringBuilder sb = new StringBuilder();
      for (Object o : objects)
         sb.append(o).append(" ");
      return sb.toString().trim();
   }

   public static void logMsg(Object... msg) {
      System.out.println(objectsToString(msg));
   }

   @Test
   public void cancelVoidTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("cancelvoid?stage=0");
      assertStatus(getResponse(suspend), Status.SERVICE_UNAVAILABLE);
      assertString(cancel, Resource.TRUE);
   }

   @Test
   public void cancelVoidOnResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> cancel = invokeRequest("cancelvoid?stage=1");
      assertString(cancel, Resource.FALSE);
   }

   @Test
   public void cancelVoidOnCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> cancel = invokeRequest("cancelvoid?stage=1");
      assertString(cancel, Resource.TRUE);
   }

   @Test
   public void resumeCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> resumeCanceled = invokeRequest("resume?stage=1", "");
      assertString(resumeCanceled, Resource.FALSE);
   }

   @Test
   public void cancelIntTest()throws Exception {
      String seconds = "20";
      invokeClear();

      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("cancelretry?stage=0", seconds);
      Response response = getResponse(suspend);
      assertStatus(response, Status.SERVICE_UNAVAILABLE);
      assertString(cancel, Resource.TRUE);
      String retry = response.getHeaderString(HttpHeaders.RETRY_AFTER);
      assertEquals(seconds, retry, "Unexpected", HttpHeaders.RETRY_AFTER,
              "header value received", retry, "expected", seconds);
      logMsg("Found expected", HttpHeaders.RETRY_AFTER, "=", retry);
   }
   
   protected static void assertEquals(Object expected, Object actual, Object... msg) {
      Assert.assertEquals(objectsToString(msg), expected, actual);
   }

   @Test
   public void cancelIntOnResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> cancel = invokeRequest("cancelretry?stage=1", "20");
      assertString(cancel, Resource.FALSE);
   }

   @Test
   public void cancelIntOnCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> cancel = invokeRequest("cancelretry?stage=1", "20");
      assertString(cancel, Resource.TRUE);
   }

   @Test
   public void resumeCanceledIntTest()throws Exception {
      cancelIntTest();
      Future<Response> resume = invokeRequest("resume?stage=1", "");
      assertString(resume, Resource.FALSE);
   }

   @Test
   public void cancelDateTest()throws Exception {
      long milis = (System.currentTimeMillis() / 1000) * 1000 + 20000;
      invokeClear();

      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("canceldate?stage=0", milis);
      Response response = getResponse(suspend);
      assertStatus(response, Status.SERVICE_UNAVAILABLE);
      assertString(cancel, Resource.TRUE);
      String header = response.getHeaderString(HttpHeaders.RETRY_AFTER);
      TimeZone timezone = findTimeZoneInDate(header);
      Date retry = null;
      try {
         retry = createDateFormat(timezone).parse(header);
      } catch (ParseException e) {
         throw new Exception(e);
      }
      assertEquals(new Date(milis), retry, "Unexpected",
              HttpHeaders.RETRY_AFTER, "header value received",
              retry.getTime(), "expected", milis);
      logMsg("Found expected", HttpHeaders.RETRY_AFTER, "=", header);
   }

   public static final TimeZone findTimeZoneInDate(String date){
      StringBuilder sb = new StringBuilder();
      StringBuilder dateBuilder = new StringBuilder(date.trim()).reverse();
      int index = 0;
      char c;
      while ((c = dateBuilder.charAt(index++)) != ' '){
         sb.append(c);
      }
      TimeZone timezone = TimeZone.getTimeZone(sb.reverse().toString());
      return timezone;
   }

   public static final DateFormat createDateFormat(TimeZone timezone){
      SimpleDateFormat sdf = new SimpleDateFormat(
              "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      sdf.setTimeZone(timezone);
      return sdf;
   }


   @Test
   public void cancelDateOnResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> cancel = invokeRequest("canceldate?stage=1",
              System.currentTimeMillis());
      assertString(cancel, Resource.FALSE);
   }

   @Test
   public void cancelDateOnCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> cancel = invokeRequest("canceldate?stage=1",
              System.currentTimeMillis());
      assertString(cancel, Resource.TRUE);
   }

   @Test
   public void resumeCanceledDateTest()throws Exception {
      cancelDateTest();
      Future<Response> resumeResumed = invokeRequest("resume?stage=1", "");
      assertString(resumeResumed, Resource.FALSE);
   }

   @Test
   public void isCanceledWhenCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> is = invokeRequest("iscanceled?stage=1");
      assertString(is, Resource.TRUE);
   }

   @Test
   public void isCanceledWhenSuspendedTest()throws Exception {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("iscanceled?stage=0");
      assertString(is, Resource.FALSE);
   }

   @Test
   public void isCanceledWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> is = invokeRequest("iscanceled?stage=1");
      assertString(is, Resource.FALSE);
   }

   @Test
   public void isDoneWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> is = invokeRequest("isdone?stage=1");
      assertString(is, Resource.TRUE);
   }

   @Test
   public void isDoneWhenSuspendedTest()throws Exception {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("isdone?stage=0");
      assertString(is, Resource.FALSE);
   }

   @Test
   public void isDoneWhenCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> is = invokeRequest("isdone?stage=1");
      assertString(is, Resource.TRUE);
   }

   @Test
   public void isDoneWhenTimedOutTest()throws Exception {
      setTimeoutTest();
      Future<Response> is = invokeRequest("isdone?stage=1");
      assertString(is, Resource.TRUE);
   }

   @Test
   public void isSuspendedWhenSuspendedTest()throws Exception {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("issuspended?stage=0");
      assertString(is, Resource.TRUE);
   }

   @Test
   public void isSuspendedWhenCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> is = invokeRequest("issuspended?stage=1");
      assertString(is, Resource.FALSE);
   }

   @Test
   public void isSuspendedWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> is = invokeRequest("issuspended?stage=1");
      assertString(is, Resource.FALSE);
   }

   @Test
   public void suspendResumeTest()throws Exception {
      invokeClear();
      String expectedResponse = "Expected response";
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resume?stage=0",
              expectedResponse);
      assertString(resume, Resource.TRUE);
      assertString(suspend, expectedResponse);
   }

   @Test
   public void resumeAnyJavaObjectInputStreamTest()throws Exception {
      invokeClear();
      String expectedResponse = "Expected response";
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resume?stage=0",
              new ByteArrayInputStream(expectedResponse.getBytes()));
      assertString(resume, Resource.TRUE);
      assertString(suspend, expectedResponse);
   }

   @Test
   public void resumeResumedTest()throws Exception {
      suspendResumeTest(); // resume & store
      Future<Response> resumeResumed = invokeRequest("resume?stage=1", "");
      assertString(resumeResumed, Resource.FALSE);
   }

   @Test
   public void resumeWithCheckedExceptionTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resumechecked?stage=0");
      assertString(resume, Resource.TRUE);
      assertException(suspend, IOException.class);
   }

   @Test
   public void resumeWithRuntimeExceptionTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resumeruntime?stage=0");
      assertString(resume, Resource.TRUE);
      assertException(suspend, RuntimeException.class);
   }

   @Test
   public void resumeWithExceptionReturnsFalseWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> resume = invokeRequest("resumechecked?stage=1");
      assertString(resume, Resource.FALSE);
   }

   @Test
   public void setTimeoutTest()throws Exception {
      invokeClear();
      logMsg("here 1");
      Future<Response> suspend = invokeRequest("suspend");
      logMsg("here 2");
      Future<Response> setTimeout = invokeRequest("settimeout?stage=0", 200);
      logMsg("here 3");
      assertStatus(getResponse(setTimeout), Status.NO_CONTENT);
      logMsg("here 4");
      // WebApplication exception with 503 is caught by ServiceUnavailableExceptionMapper
      Response fromMapper = getResponse(suspend);
      logMsg("here 5");
      assertStatus(fromMapper, Status.REQUEST_TIMEOUT);
      String entity = fromMapper.readEntity(String.class);
      assertContains(entity, 503);
      logMsg("Found expected status 503");
   }

   @Test
   public void updateTimeoutTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("settimeout?stage=0",
              600000);
      assertStatus(getResponse(setTimeout), Status.NO_CONTENT);
      assertFalse(suspend.isDone(),
              "Suspended AsyncResponse already received");
      setTimeout = invokeRequest("settimeout?stage=1", 200);
      assertStatus(getResponse(setTimeout), Status.NO_CONTENT);
      // WebApplication exception with 503 is caught by ServiceUnavailableExceptionMapper
      Response fromMapper = getResponse(suspend);
      assertStatus(fromMapper, Status.REQUEST_TIMEOUT);
      String entity = fromMapper.readEntity(String.class);
      assertContains(entity, 503);
      logMsg("Found expected status 503");
   }

   @Test
   public void handleTimeOutWaitsForeverTest()throws Exception {
      String responseMsg = "handleTimeOutWaitsForeverTest";
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("timeouthandler?stage=0", 1);
      Future<Response> resume = invokeRequest("resume?stage=1", responseMsg);
      assertStatus(getResponse(setTimeout), Status.NO_CONTENT);
      assertString(resume, Resource.TRUE);
      assertString(suspend, responseMsg);
   }

   @Test
   public void handleTimeoutCancelsTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("timeouthandler?stage=0", 2);
      assertStatus(getResponse(setTimeout), Status.NO_CONTENT);
      assertStatus(getResponse(suspend), Status.SERVICE_UNAVAILABLE);
      Future<Response> resume = invokeRequest("issuspended?stage=1");
      assertString(resume, Resource.FALSE);

   }

   @Test
   public void handleTimeoutResumesTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> setTimeout = invokeRequest("timeouthandler?stage=0", 3);
      assertStatus(getResponse(setTimeout), Status.NO_CONTENT);
      assertString(suspend, Resource.RESUMED);
      Future<Response> resume = invokeRequest("issuspended?stage=1");
      assertString(resume, Resource.FALSE);

   }

   protected String getAbsoluteUrl() {
      return "http://localhost:8080/resource";
   }


   private void invokeClear()throws Exception {
      Response response = client.target(getAbsoluteUrl()).path("clear").request().get();
      Assert.assertEquals(204, response.getStatus());
   }

   private Future<Response> invokeRequest(String resource) {
      AsyncInvoker async = createAsyncInvoker(resource);
      Future<Response> future = async.get();
      return future;
   }

   private <T> Future<Response> invokeRequest(String resource, T entity) {
      AsyncInvoker async = createAsyncInvoker(resource);
      Future<Response> future = async.post(Entity.entity(entity,
              MediaType.TEXT_PLAIN_TYPE));
      return future;
   }

   private WebTarget createWebTarget(String resource) {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(getAbsoluteUrl() + "/" + resource);
      return target;
   }

   private AsyncInvoker createAsyncInvoker(String resource) {
      WebTarget target = createWebTarget(resource);
      AsyncInvoker async = target.request().async();
      return async;
   }

   private static Response getResponse(Future<Response> future)throws Exception {
      Response response = future.get();
      return response;
   }

   private static void assertStatus(Response response, Response.Status status)
          throws Exception {
      assertEquals(response.getStatus(), status.getStatusCode(),
              "Unexpected status code received", response.getStatus(),
              "expected was", status);
      logMsg("Found expected status", status);
   }

   private static void assertString(Future<Response> future, String check)
          throws Exception {
      Response response = getResponse(future);
      assertStatus(response, Status.OK);
      String content = response.readEntity(String.class);
      assertEquals(check, content, "Unexpected response content", content);
      logMsg("Found expected string", check);
   }

   private static void assertException(Future<Response> future,
                                       Class<? extends Throwable> e)throws Exception {
      String clazz = e.getName();
      Response response = getResponse(future);
      assertStatus(response, Response.Status.NOT_ACCEPTABLE);
      assertContains(response.readEntity(String.class), clazz, clazz,
              "not thrown");
      logMsg(clazz, "has been thrown as expected");
   }

   public static void assertContains(String string, String substring,
                                     Object... message)throws Exception {
      assertTrue(string.contains(substring), message);
   }

   public static <T> void assertContains(T text, T subtext, Object... message)
          throws Exception {
      assertContains(text.toString(), subtext.toString(), message);
   }

   public static void assertTrue(boolean condition, Object... message)  {
      if (!condition)
         Assert.fail(objectsToString(message));
   }

   public static void //
   assertFalse(boolean condition, Object... message)throws Exception {
      assertTrue(!condition, message);
   }

}