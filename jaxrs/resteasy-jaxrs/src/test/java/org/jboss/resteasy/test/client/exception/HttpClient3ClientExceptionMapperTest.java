package org.jboss.resteasy.test.client.exception;

import org.apache.commons.httpclient.CircularRedirectException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HttpContentTooLargeException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.InvalidRedirectLocationException;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.RedirectException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthChallengeException;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyBuilder;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.client.exception.ResteasyAuthChallengeException;
import org.jboss.resteasy.client.exception.ResteasyAuthenticationException;
import org.jboss.resteasy.client.exception.ResteasyCircularRedirectException;
import org.jboss.resteasy.client.exception.ResteasyConnectTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyConnectionPoolTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyCredentialsNotAvailableException;
import org.jboss.resteasy.client.exception.ResteasyHttpContentTooLargeException;
import org.jboss.resteasy.client.exception.ResteasyHttpException;
import org.jboss.resteasy.client.exception.ResteasyHttpRecoverableException;
import org.jboss.resteasy.client.exception.ResteasyIOException;
import org.jboss.resteasy.client.exception.ResteasyInvalidCredentialsException;
import org.jboss.resteasy.client.exception.ResteasyInvalidRedirectLocationException;
import org.jboss.resteasy.client.exception.ResteasyMalformedChallengeException;
import org.jboss.resteasy.client.exception.ResteasyMalformedCookieException;
import org.jboss.resteasy.client.exception.ResteasyNoHttpResponseException;
import org.jboss.resteasy.client.exception.ResteasyProtocolException;
import org.jboss.resteasy.client.exception.ResteasyRedirectException;
import org.jboss.resteasy.client.exception.ResteasyURIException;
import org.jboss.resteasy.client.exception.mapper.ApacheHttpClient3ExceptionMapper;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.Types;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *  
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 1, 2012
 */
@SuppressWarnings("deprecation")
public class HttpClient3ClientExceptionMapperTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   public static interface Foo
   {
      @GET
      @Path("foo")
      String getFoo();

      @PUT
      @Path("foo")
      String setFoo(String value);
   }

   @Path("foo")
   public static class FooImpl implements Foo
   {
      @Context
      HttpRequest request;

      @Override
      public String getFoo()
      {
         return request.getHttpHeaders().getAcceptableMediaTypes().toString();
      }

      @Override
      public String setFoo(String value)
      {
         return request.getHttpHeaders().getMediaType().toString();
      }
   }

   @Path("/")
   public static class TestResource
   {
      @POST
      public String doPost()
      {
         return "abc";
      }
   }

   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void testApacheHttpClient3Executor() throws Exception
   {
      doTestApacheHttpClient3Executor();
   }

   /**
    * Verify that ApacheHttpClient4ExceptionMapper always gets set.
    */
   @Test
   public void testApacheHttpClient3ExecutorAgain() throws Exception
   {
      doTestApacheHttpClient3Executor();
   }

   private void doTestApacheHttpClient3Executor() throws Exception
   {
      before();
      try
      {
         Foo foo = ProxyBuilder.build(Foo.class, "http://localhost:9999")
                               .executor(new ApacheHttpClientExecutor())
                               .serverMediaType(MediaType.TEXT_PLAIN_TYPE).now();
         foo.getFoo();
      }
      catch (ResteasyIOException e)
      {
         traverseException(e);
         assertTrue(ConnectException.class.equals(e.getCause().getClass()));
      }
      catch (Throwable t)
      {
         traverseException(t);
         fail("Expected HttpHostConnectException, got " + t);
      }
      finally
      {
         after();
      }
   }

   @Test
   public void testAuthenticationException() throws Exception
   {
      doTest(new ResteasyAuthenticationException(), new AuthenticationException());
   }

   @Test
   public void testAuthChallengeException() throws Exception
   {
      doTest(new ResteasyAuthChallengeException(), new AuthChallengeException());
   }
   
   @Test
   public void testCircularRedirectException() throws Exception
   {
      doTest(new ResteasyCircularRedirectException(), new CircularRedirectException());
   }
   
   @Test
   public void testConnectionPoolTimeoutException() throws Exception
   {
      doTest(new ResteasyConnectionPoolTimeoutException(), new ConnectionPoolTimeoutException());
   }
   
   @Test
   public void testConnectTimeoutException() throws Exception
   {
      doTest(new ResteasyConnectTimeoutException(), new ConnectTimeoutException());
   }
   
   @Test
   public void testCredentialsNotAvailableException() throws Exception
   {
      doTest(new ResteasyCredentialsNotAvailableException(), new CredentialsNotAvailableException());
   }
   
   @Test
   public void testHttpContentTooLargeException() throws Exception
   {
      doTest(new ResteasyHttpContentTooLargeException(), new HttpContentTooLargeException("", 2));
   }
   
   @Test
   public void testHttpException() throws Exception
   {
      doTest(new ResteasyHttpException(), new HttpException());
   }
   
   @Test
   public void testHttpRecoverableException() throws Exception
   {
      doTest(new ResteasyHttpRecoverableException(), new HttpRecoverableException());
   }
   
   @Test
   public void testInvalidCredentialsException() throws Exception
   {
      doTest(new ResteasyInvalidCredentialsException(), new InvalidCredentialsException());
   }
   
   @Test
   public void testInvalidRedirectLocationException() throws Exception
   {
      doTest(new ResteasyInvalidRedirectLocationException(), new InvalidRedirectLocationException("", ""));
   }
   
   @Test
   public void testIOException() throws Exception
   {
      doTest(new ResteasyIOException(), new IOException());
   }

   @Test
   public void testMalformedChallengeException() throws Exception
   {
      doTest(new ResteasyMalformedChallengeException(), new MalformedChallengeException());
   }

   @Test
   public void testMalformedCookieException() throws Exception
   {
      doTest(new ResteasyMalformedCookieException(), new MalformedCookieException());
   }

   @Test
   public void testNoHttpResponseException() throws Exception
   {
      doTest(new ResteasyNoHttpResponseException(), new NoHttpResponseException(""));
   }

   @Test
   public void testProtocolException() throws Exception
   {
      doTest(new ResteasyProtocolException(), new ProtocolException());
   }

   @Test
   public void testRedirectException() throws Exception
   {
      doTest(new ResteasyRedirectException(), new RedirectException());
   }

   @Test
   public void testURIException() throws Exception
   {
      doTest(new ResteasyURIException(), new URIException());
   }
   
   private void doTest(Exception resteasyException, Exception embeddedException) throws Exception
   {
      before();
      try
      {
         ProxyBuilder<Foo> builder = ProxyBuilder.build(Foo.class, "http://localhost:9999").serverMediaType(MediaType.TEXT_PLAIN_TYPE);
         builder.executor(new TestClientExecutor(embeddedException));
         Foo foo = builder.now();
         foo.getFoo();
      }
      catch (Throwable t)
      {
         traverseException(t);
         assertTrue("Expected instance of " + resteasyException.getClass() + ", got " + t, resteasyException.getClass().equals(t.getClass()));
         assertTrue("Expected instance of " + embeddedException.getClass() + ", got " + t, embeddedException.getClass().equals(t.getCause().getClass()));
      }
      finally
      {
         after();
      }
   }

   static private void traverseException(Throwable t)
   {
      String indent = "";
      while (t instanceof Throwable)
      {
         System.out.println(indent + t);
         t = t.getCause();
         indent += "  ";
      }
   }
   
   static public class TestApplication extends Application
   {
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestClientExceptionMapper.class);
         return classes;
      }
   }
   

   @Provider
   static class TestClientExecutor implements ClientExecutor
   {
      private Exception e;

      public TestClientExecutor(Exception e)
      {
         this.e = e;
         if (ResteasyProviderFactory.getInstance().getClientExceptionMapper(Exception.class) == null)
         {
            Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(ApacheHttpClient3ExceptionMapper.class, ClientExceptionMapper.class)[0];
            ResteasyProviderFactory.getInstance().addClientExceptionMapper(new ApacheHttpClient3ExceptionMapper(), exceptionType);
         }
      }
      @Override
      public ClientRequest createRequest(String uriTemplate)
      {
         return null;
      }
      @Override
      public ClientRequest createRequest(UriBuilder uriBuilder)
      {
         return null;
      }
      @Override
      public ClientResponse<?> execute(ClientRequest request) throws Exception
      {
         throw e;
      }
      @Override
      public void close() throws Exception
      {
      }
   }
   
   static class TestException extends RuntimeException
   {
      private static final long serialVersionUID = -7825447948319726641L;
      
      public TestException(Exception e)
      {
         super(e);
      }
   }
   
   @Provider
   static public class TestClientExceptionMapper implements ClientExceptionMapper<Exception>
   {
      @Override
      public RuntimeException toException(Exception exception)
      {
         return new TestException(exception);
      }
      
   }
}
