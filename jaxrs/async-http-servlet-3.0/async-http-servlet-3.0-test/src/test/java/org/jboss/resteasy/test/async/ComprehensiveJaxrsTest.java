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

   /*
    * @testName: cancelVoidTest
    * @assertion_ids: JAXRS:JAVADOC:980;
    * @test_Strategy: Cancel the suspended request processing. When a request 
    * 					processing is cancelled using this method, the JAX-RS 
    * 					implementation MUST indicate to the client that the 
    * 					request processing has been cancelled by sending back 
    * 					a HTTP 503 (Service unavailable) error response.
    */
   @Test
   public void cancelVoidTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> cancel = invokeRequest("cancelvoid?stage=0");
      assertStatus(getResponse(suspend), Status.SERVICE_UNAVAILABLE);
      assertString(cancel, Resource.TRUE);
   }

   /*
    * @testName: cancelVoidOnResumedTest
    * @assertion_ids: JAXRS:JAVADOC:980;
    * @test_Strategy: Cancel the suspended request processing.  Invoking a 
    * 					cancel(...) method on an asynchronous response instance 
    * 					that has already been resumed has no effect and the 
    * 					method call is ignored while returning false when resumed.
    */
   @Test
   public void cancelVoidOnResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> cancel = invokeRequest("cancelvoid?stage=1");
      assertString(cancel, Resource.FALSE);
   }

   /*
    * @testName: cancelVoidOnCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:980;
    * @test_Strategy: Cancel the suspended request processing.  Invoking a 
    * 					cancel(...) method on an asynchronous response instance 
    * 					that has already been resumed has no effect and the 
    * 					method call is ignored while returning true when canceled.
    */
   @Test
   public void cancelVoidOnCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> cancel = invokeRequest("cancelvoid?stage=1");
      assertString(cancel, Resource.TRUE);
   }

   /*
    * @testName: resumeCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:980;
    * @test_Strategy: returns false in case the request processing is not 
    * 							suspended and could not be resumed.
    */
   @Test
   public void resumeCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> resumeCanceled = invokeRequest("resume?stage=1", "");
      assertString(resumeCanceled, Resource.FALSE);
   }

   /*
    * @testName: cancelIntTest
    * @assertion_ids: JAXRS:JAVADOC:980;
    * @test_Strategy: Cancel the suspended request processing. When a request 
    * 					processing is cancelled using this method, the JAX-RS 
    * 					implementation MUST indicate to the client that the 
    * 					request processing has been cancelled by sending back 
    * 					a HTTP 503 (Service unavailable) error response with a 
    * 					Retry-After header set to the value provided by the 
    * 					method parameter.
    */
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

   /*
    * @testName: cancelIntOnResumedTest
    * @assertion_ids: JAXRS:JAVADOC:981;
    * @test_Strategy: Cancel the suspended request processing.  Invoking a 
    * 					cancel(...) method on an asynchronous response instance 
    * 					that has already been resumed has no effect and the 
    * 					method call is ignored while returning false when resumed
    */
   @Test
   public void cancelIntOnResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> cancel = invokeRequest("cancelretry?stage=1", "20");
      assertString(cancel, Resource.FALSE);
   }

   /*
    * @testName: cancelIntOnCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:981;
    * @test_Strategy: Cancel the suspended request processing.  Invoking a 
    * 					cancel(...) method on an asynchronous response instance 
    * 					that has already been resumed has no effect and the 
    * 					method call is ignored while returning true when canceled
    */
   @Test
   public void cancelIntOnCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> cancel = invokeRequest("cancelretry?stage=1", "20");
      assertString(cancel, Resource.TRUE);
   }

   /*
    * @testName: resumeCanceledIntTest
    * @assertion_ids: JAXRS:JAVADOC:981;
    * @test_Strategy: returns false in case the request processing is not 
    * 							suspended and could not be resumed.
    */
   @Test
   public void resumeCanceledIntTest()throws Exception {
      cancelIntTest();
      Future<Response> resume = invokeRequest("resume?stage=1", "");
      assertString(resume, Resource.FALSE);
   }

   /*
    * @testName: cancelDateTest
    * @assertion_ids: JAXRS:JAVADOC:982;
    * @test_Strategy: Cancel the suspended request processing. When a request 
    * 					processing is cancelled using this method, the JAX-RS 
    * 					implementation MUST indicate to the client that the 
    * 					request processing has been cancelled by sending back 
    * 					a HTTP 503 (Service unavailable) error response with a 
    * 					Retry-After header set to the value provided by the 
    * 					method parameter.
    */
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


   /*
    * @testName: cancelDateOnResumedTest
    * @assertion_ids: JAXRS:JAVADOC:982;
    * @test_Strategy: Cancel the suspended request processing.  Invoking a 
    * 					cancel(...) method on an asynchronous response instance 
    * 					that has already been resumed has no effect and the 
    * 					method call is ignored while returning false when resumed
    */
   @Test
   public void cancelDateOnResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> cancel = invokeRequest("canceldate?stage=1",
              System.currentTimeMillis());
      assertString(cancel, Resource.FALSE);
   }

   /*
    * @testName: cancelDateOnCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:982;
    * @test_Strategy: Cancel the suspended request processing.  Invoking a 
    * 					cancel(...) method on an asynchronous response instance 
    * 					that has already been resumed has no effect and the 
    * 					method call is ignored while returning true when canceled
    */
   @Test
   public void cancelDateOnCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> cancel = invokeRequest("canceldate?stage=1",
              System.currentTimeMillis());
      assertString(cancel, Resource.TRUE);
   }

   /*
    * @testName: resumeCanceledDateTest
    * @assertion_ids: JAXRS:JAVADOC:982;
    * @test_Strategy:  returns false in case the request processing is not 
    * 							suspended and could not be resumed.
    */
   @Test
   public void resumeCanceledDateTest()throws Exception {
      cancelDateTest();
      Future<Response> resumeResumed = invokeRequest("resume?stage=1", "");
      assertString(resumeResumed, Resource.FALSE);
   }

   /*
    * @testName: isCanceledWhenCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:628;
    * @test_Strategy: Check if the asynchronous response instance has been 
    * 					cancelled. Method returns true if this asynchronous 
    * 					response has been canceled before completion.
    */
   @Test
   public void isCanceledWhenCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> is = invokeRequest("iscanceled?stage=1");
      assertString(is, Resource.TRUE);
   }

   /*
    * @testName: isCanceledWhenSuspendedTest
    * @assertion_ids: JAXRS:JAVADOC:628;
    * @test_Strategy: Check if the asynchronous response instance has been 
    * 					cancelled. Method returns true if this asynchronous 
    * 					response has been canceled before completion.
    */
   @Test
   public void isCanceledWhenSuspendedTest()throws Exception {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("iscanceled?stage=0");
      assertString(is, Resource.FALSE);
   }

   /*
    * @testName: isCanceledWhenResumedTest
    * @assertion_ids: JAXRS:JAVADOC:628;
    * @test_Strategy: Check if the asynchronous response instance has been 
    * 					cancelled. Method returns true if this asynchronous 
    * 					response has been canceled before completion.
    */
   @Test
   public void isCanceledWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> is = invokeRequest("iscanceled?stage=1");
      assertString(is, Resource.FALSE);
   }

   /*
    * @testName: isDoneWhenResumedTest
    * @assertion_ids: JAXRS:JAVADOC:629;
    * @test_Strategy: Check if the processing of a request this asynchronous 
    * 					response instance belongs to has finished. Method 
    * 					returns true if the processing of a request this 
    * 					asynchronous response is bound to is finished. 
    * 					The request processing may be finished due to a normal 
    * 					termination, a suspend timeout, or cancellation -- in 
    * 					all of these cases, this method will return true.
    */
   @Test
   public void isDoneWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> is = invokeRequest("isdone?stage=1");
      assertString(is, Resource.TRUE);
   }

   /*
    * @testName: isDoneWhenSuspendedTest
    * @assertion_ids: JAXRS:JAVADOC:629;
    * @test_Strategy: Check if the processing of a request this asynchronous 
    * 					response instance belongs to has finished. Method 
    * 					returns true if the processing of a request this 
    * 					asynchronous response is bound to is finished. 
    * 					The request processing may be finished due to a normal 
    * 					termination, a suspend timeout, or cancellation -- in 
    * 					all of these cases, this method will return true.
    */
   @Test
   public void isDoneWhenSuspendedTest()throws Exception {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("isdone?stage=0");
      assertString(is, Resource.FALSE);
   }

   /*
    * @testName: isDoneWhenCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:629;
    * @test_Strategy: Check if the processing of a request this asynchronous 
    * 					response instance belongs to has finished. Method 
    * 					returns true if the processing of a request this 
    * 					asynchronous response is bound to is finished. 
    * 					The request processing may be finished due to a normal 
    * 					termination, a suspend timeout, or cancellation -- in 
    * 					all of these cases, this method will return true.
    */
   @Test
   public void isDoneWhenCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> is = invokeRequest("isdone?stage=1");
      assertString(is, Resource.TRUE);
   }

   /*
    * @testName: isDoneWhenTimedOutTest
    * @assertion_ids: JAXRS:JAVADOC:629;
    * @test_Strategy: Check if the processing of a request this asynchronous 
    * 					response instance belongs to has finished. Method 
    * 					returns true if the processing of a request this 
    * 					asynchronous response is bound to is finished. 
    * 					The request processing may be finished due to a normal 
    * 					termination, a suspend timeout, or cancellation -- in 
    * 					all of these cases, this method will return true.
    */
   @Test
   public void isDoneWhenTimedOutTest()throws Exception {
      setTimeoutTest();
      Future<Response> is = invokeRequest("isdone?stage=1");
      assertString(is, Resource.TRUE);
   }

   /*
    * @testName: isSuspendedWhenSuspendedTest
    * @assertion_ids: JAXRS:JAVADOC:630;
    * @test_Strategy: Check if the asynchronous response instance is in a 
    * 					suspended state. Method returns true if this 
    * 					asynchronous response is still suspended and has not 
    * 					finished processing yet (either by resuming or 
    * 					canceling the response).
    */
   @Test
   public void isSuspendedWhenSuspendedTest()throws Exception {
      invokeClear();
      invokeRequest("suspend");
      Future<Response> is = invokeRequest("issuspended?stage=0");
      assertString(is, Resource.TRUE);
   }

   /*
    * @testName: isSuspendedWhenCanceledTest
    * @assertion_ids: JAXRS:JAVADOC:630;
    * @test_Strategy: Check if the asynchronous response instance is in a 
    * 					suspended state. Method returns true if this 
    * 					asynchronous response is still suspended and has not 
    * 					finished processing yet (either by resuming or 
    * 					canceling the response).
    */
   @Test
   public void isSuspendedWhenCanceledTest()throws Exception {
      cancelVoidTest();
      Future<Response> is = invokeRequest("issuspended?stage=1");
      assertString(is, Resource.FALSE);
   }

   /*
    * @testName: isSuspendedWhenResumedTest
    * @assertion_ids: JAXRS:JAVADOC:630;
    * @test_Strategy: Check if the asynchronous response instance is in a 
    * 					suspended state. Method returns true if this 
    * 					asynchronous response is still suspended and has not 
    * 					finished processing yet (either by resuming or 
    * 					canceling the response).
    */
   @Test
   public void isSuspendedWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> is = invokeRequest("issuspended?stage=1");
      assertString(is, Resource.FALSE);
   }

   /*
    * @testName: suspendResumeTest
    * @assertion_ids: JAXRS:JAVADOC:983;
    * @test_Strategy: Resume the suspended request processing using the 
    * 					provided response data.
    */
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

   /*
    * @testName: resumeAnyJavaObjectInputStreamTest
    * @assertion_ids: JAXRS:JAVADOC:983;
    * @test_Strategy: Resume the suspended request processing using the 
    * 					provided response data. The provided response data can 
    * 					be of any Java type that can be returned from a JAX-RS 
    * 					resource method.
    */
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

   /*
    * @testName: resumeResumedTest
    * @assertion_ids: JAXRS:JAVADOC:983;
    * @test_Strategy: returns false in case the request processing is not 
    * 					suspended and could not be resumed.
    */
   @Test
   public void resumeResumedTest()throws Exception {
      suspendResumeTest(); // resume & store
      Future<Response> resumeResumed = invokeRequest("resume?stage=1", "");
      assertString(resumeResumed, Resource.FALSE);
   }

   /*
    * @testName: resumeWithCheckedExceptionTest
    * @assertion_ids: JAXRS:JAVADOC:641;
    * @test_Strategy: Resume the suspended request processing using the 
    * 					provided throwable. For the provided throwable same 
    * 					rules apply as for an exception thrown by a JAX-RS 
    * 					resource method.
    */
   @Test
   public void resumeWithCheckedExceptionTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resumechecked?stage=0");
      assertString(resume, Resource.TRUE);
      assertException(suspend, IOException.class);
   }

   /*
    * @testName: resumeWithRuntimeExceptionTest
    * @assertion_ids: JAXRS:JAVADOC:984;
    * @test_Strategy: Resume the suspended request processing using the 
    * 					provided throwable. For the provided throwable same 
    * 					rules apply as for an exception thrown by a JAX-RS 
    * 					resource method.
    */
   @Test
   public void resumeWithRuntimeExceptionTest()throws Exception {
      invokeClear();
      Future<Response> suspend = invokeRequest("suspend");
      Future<Response> resume = invokeRequest("resumeruntime?stage=0");
      assertString(resume, Resource.TRUE);
      assertException(suspend, RuntimeException.class);
   }

   /*
    * @testName: resumeWithExceptionReturnsFalseWhenResumedTest
    * @assertion_ids: JAXRS:JAVADOC:984;
    * @test_Strategy:  returns false in case the request processing is not 
    * 					suspended and could not be resumed.
    */
   @Test
   public void resumeWithExceptionReturnsFalseWhenResumedTest()throws Exception {
      suspendResumeTest();
      Future<Response> resume = invokeRequest("resumechecked?stage=1");
      assertString(resume, Resource.FALSE);
   }

   /*
    * @testName: setTimeoutTest
    * @assertion_ids: JAXRS:JAVADOC:1034;
    * 					JAXRS:SPEC:103; JAXRS:SPEC:104;
    * @test_Strategy: The new suspend timeout values override any timeout 
    * 					value previously specified.
    * 
    * 					JAX-RS implementations are REQUIRED to generate a 
    * 					ServiceUnavailableException, a subclass of 
    * 					WebApplicationException with its status set to 503, if 
    * 					the timeout value is reached and no timeout handler is 
    * 					registered.
    * 
    * 					The exception MUST be processed as described in section 3.3.4.
    */
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

   /*
    * @testName: updateTimeoutTest
    * @assertion_ids: JAXRS:JAVADOC:1034;
    * 					JAXRS:SPEC:103; JAXRS:SPEC:104;
    * @test_Strategy: Set/update the suspend timeout.
    * 
    * 					JAX-RS implementations are REQUIRED to generate a 
    * 					ServiceUnavailableException, a subclass of 
    * 					WebApplicationException with its status set to 503, if 
    * 					the timeout value is reached and no timeout handler is 
    * 					registered.
    * 
    * 					The exception MUST be processed as described in section 3.3.4.
    */
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

   /*
    * @testName: handleTimeOutWaitsForeverTest
    * @assertion_ids: JAXRS:JAVADOC:725;
    * 					JAXRS:JAVADOC:645;
    * 					JAXRS:SPEC:105;
    * @test_Strategy:  Invoked when the suspended asynchronous response is 
    * 					about to time out.
    * 
    * 					Set/replace a time-out handler for the suspended 
    * 					asynchronous response.
    * 
    * 					If a registered timeout handler resets the timeout 
    * 					value or resumes the connection and returns a response, 
    * 					JAX-RS implementations MUST NOT generate an exception.
    */
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

   /*
    * @testName: handleTimeoutCancelsTest
    * @assertion_ids: JAXRS:JAVADOC:725;
    * 					JAXRS:JAVADOC:645; 					
    * @test_Strategy:  Invoked when the suspended asynchronous response is 
    * 					about to time out.
    * 
    * 					Set/replace a time-out handler for the suspended 
    * 					asynchronous response.
    */
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

   /*
    * @testName: handleTimeoutResumesTest
    * @assertion_ids: JAXRS:JAVADOC:725;
    * 					JAXRS:JAVADOC:645;
    * 					JAXRS:SPEC:105;
    * @test_Strategy:  Invoked when the suspended asynchronous response is 
    * 					about to time out.
    * 
    * 					Set/replace a time-out handler for the suspended 
    * 					asynchronous response.
    * 
    * 					If a registered timeout handler resets the timeout 
    * 					value or resumes the connection and returns a response, 
    * 					JAX-RS implementations MUST NOT generate an exception.
    */
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

   // ////////////////////////////////////////////////////////////////////////////

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

   public static void assertContainsIgnoreCase(String string,
                                               String substring, Object... message)throws Exception {
      assertTrue(string.toLowerCase().contains(substring.toLowerCase()),
              message);
   }

   public static <T> void assertContains(T text, T subtext, Object... message)
          throws Exception {
      assertContains(text.toString(), subtext.toString(), message);
   }

   public static <T> void assertContainsIgnoreCase(T text, T subtext,
                                                   Object... message)throws Exception {
      assertContainsIgnoreCase(text.toString(), subtext.toString(), message);
   }

   protected static <T extends Throwable> T assertCause(Throwable parent,
                                                        Class<T> wrapped, Object... msg)throws Exception {
      T t = hasCause(parent, wrapped);
      Assert.assertNotNull(objectsToString(msg), t);
      return t;
   }

   public static void assertTrue(boolean condition, Object... message)  {
      if (!condition)
         Assert.fail(objectsToString(message));
   }

   public static void //
   assertFalse(boolean condition, Object... message)throws Exception {
      assertTrue(!condition, message);
   }

   @SuppressWarnings("unchecked")
   private static <T extends Throwable> T //
   hasCause(Throwable parent, Class<? extends Throwable> cause) {
      while (parent != null) {
         if (cause.isInstance(parent))
            return (T) parent;
         parent = parent.getCause();
      }
      return null;
   }


}