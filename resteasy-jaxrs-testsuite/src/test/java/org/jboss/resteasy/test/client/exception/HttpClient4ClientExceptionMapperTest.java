package org.jboss.resteasy.test.client.exception;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.RedirectException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.auth.NTLMEngineException;
import org.apache.http.impl.client.TunnelRefusedException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyBuilder;
import org.jboss.resteasy.client.exception.ResteasyAuthenticationException;
import org.jboss.resteasy.client.exception.ResteasyCircularRedirectException;
import org.jboss.resteasy.client.exception.ResteasyClientProtocolException;
import org.jboss.resteasy.client.exception.ResteasyConnectTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyConnectionClosedException;
import org.jboss.resteasy.client.exception.ResteasyConnectionPoolTimeoutException;
import org.jboss.resteasy.client.exception.ResteasyCookieRestrictionViolationException;
import org.jboss.resteasy.client.exception.ResteasyHttpException;
import org.jboss.resteasy.client.exception.ResteasyHttpHostConnectException;
import org.jboss.resteasy.client.exception.ResteasyIOException;
import org.jboss.resteasy.client.exception.ResteasyInvalidCredentialsException;
import org.jboss.resteasy.client.exception.ResteasyMalformedChallengeException;
import org.jboss.resteasy.client.exception.ResteasyMalformedChunkCodingException;
import org.jboss.resteasy.client.exception.ResteasyMalformedCookieException;
import org.jboss.resteasy.client.exception.ResteasyMethodNotSupportedException;
import org.jboss.resteasy.client.exception.ResteasyNTLMEngineException;
import org.jboss.resteasy.client.exception.ResteasyNoHttpResponseException;
import org.jboss.resteasy.client.exception.ResteasyNonRepeatableRequestException;
import org.jboss.resteasy.client.exception.ResteasyProtocolException;
import org.jboss.resteasy.client.exception.ResteasyRedirectException;
import org.jboss.resteasy.client.exception.ResteasyTunnelRefusedException;
import org.jboss.resteasy.client.exception.ResteasyUnsupportedHttpVersionException;
import org.jboss.resteasy.client.exception.mapper.ApacheHttpClient4ExceptionMapper;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class HttpClient4ClientExceptionMapperTest
{
   private static final Logger LOG = Logger.getLogger(HttpClient4ClientExceptionMapperTest.class);
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   public interface Foo
   {
      @GET
      @Path("foo")
      String getFoo();

      @PUT
      @Path("foo")
      String setFoo(String value);
      
      @POST
      @Path("error")
      String error();
   }

   @Path("foo")
   public static class TestResource implements Foo
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

      @Override
      public String error()
      {
         throw new WebApplicationException(500);
      }
   }

   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   public void beforeProviderInstance(ClientExceptionMapper<?> mapper) throws Exception
   {
      deployment = new ResteasyDeployment();
      ArrayList<Object> providers = new ArrayList<Object>();
      providers.add(mapper);
      deployment.setProviders(providers);
      deployment.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   public void beforeApplicationClasses(final Class<?> mapper) throws Exception
   {
      deployment = new ResteasyDeployment();
      Application application = new Application()
      {
         public Set<Class<?>> getClasses()
         {
            HashSet<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(mapper);
            return classes;
         }
      };
      deployment.setApplication(application);
      deployment.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   public void beforeApplicationSingleton(final ClientExceptionMapper<?> mapper) throws Exception
   {
      deployment = new ResteasyDeployment();
      Application application = new Application()
      {
         public Set<Object> getSingletons()
         {
            HashSet<Object> singletons = new HashSet<Object>();
            singletons.add(mapper);
            return singletons;
         }
      };
      deployment.setApplication(application);
      deployment.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   public void beforeClassNames(final Class<?> mapper) throws Exception
   {
      deployment = new ResteasyDeployment();
      ArrayList<String> providerClasses = new ArrayList<String>();
      providerClasses.add(mapper.getName());
      deployment.setProviderClasses(providerClasses);
      deployment.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   public void beforeContextParams(final Class<?> mapper) throws Exception
   {      
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("javax.ws.rs.Application", TestApplication.class.getName());
      deployment = EmbeddedContainer.start(initParams, contextParams);
   }
   
   public void beforeInitParams(final Class<?> mapper) throws Exception
   {      
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      initParams.put("javax.ws.rs.Application", TestApplication.class.getName());
      deployment = EmbeddedContainer.start(initParams, contextParams);
   }

   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   /**
    * Verify that ClientResponseFailure is thrown if the request successfully invokes a
    * resource method, and the resource method returns a status code >= 400.  That is,
    * verify that the default error handling mechanism still works.
    */
   @Test
   public void testClientResponseFailure() throws Exception
   {
      before();
      boolean ok = false;
      try
      {
         Foo foo = ProxyBuilder.build(Foo.class, "http://localhost:8081/foo/").serverMediaType(MediaType.TEXT_PLAIN_TYPE).now();
         String answer = foo.error();
         LOG.info("answer: " + answer);
      }
      catch (ClientResponseFailure e)
      {
         ok = true;
      }
      catch (Throwable t)
      {
         traverseException(t);
         fail("Expected ClientResponseFailure, got " + t);
      }
      finally
      {
         after();
      }
      assertTrue("Expected ClientResponseFailure, got no Exception", ok);
   }
   
   @Test
   public void testApacheHttpClient4Executor() throws Exception
   {
      doTestApacheHttpClient4Executor();
   }

   /**
    * Verify that ApacheHttpClient4ExceptionMapper always gets set.
    */
   @Test
   public void testApacheHttpClient4ExecutorAgain() throws Exception
   {
      doTestApacheHttpClient4Executor();
   }

   private void doTestApacheHttpClient4Executor() throws Exception
   {
      before();
      try
      {
         Foo foo = ProxyBuilder.build(Foo.class, "http://localhost:9999").serverMediaType(MediaType.TEXT_PLAIN_TYPE).now();
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
   public void testCircularRedirectException() throws Exception
   {
      doTest(new ResteasyCircularRedirectException(), new CircularRedirectException());
   }


   @Test
   public void testClientProtocolException() throws Exception
   {
      doTest(new ResteasyClientProtocolException(), new ClientProtocolException());
   }

   @Test
   public void testConnectionClosedException() throws Exception
   {
      doTest(new ResteasyConnectionClosedException(), new ConnectionClosedException(""));
   }

   @Test
   public void testConnectionPoolTimeoutException() throws Exception
   {
      doTest(new ResteasyConnectionPoolTimeoutException(), new ConnectionPoolTimeoutException(""));
   }

   @Test
   public void testConnectTimeoutException() throws Exception
   {
      doTest(new ResteasyConnectTimeoutException(), new ConnectTimeoutException(""));
   }
   
   @Test
   public void testCookieRestrictionViolationException() throws Exception
   {
      doTest(new ResteasyCookieRestrictionViolationException(), new CookieRestrictionViolationException());
   }

   @Test
   public void testHttpException() throws Exception
   {
      doTest(new ResteasyHttpException(), new HttpException());
   }

   @Test
   public void testHttpHostConnectException() throws Exception
   {
      doTest(new ResteasyHttpHostConnectException(), new HttpHostConnectException(null, null));
   }
   
   @Test
   public void testInvalidCredentialsException() throws Exception
   {
      doTest(new ResteasyInvalidCredentialsException(), new InvalidCredentialsException());
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
   public void testMalformedChunkCodingException() throws Exception
   {
      doTest(new ResteasyMalformedChunkCodingException(), new MalformedChunkCodingException());
   }

   @Test
   public void testMalformedCookieException() throws Exception
   {
      doTest(new ResteasyMalformedCookieException(), new MalformedCookieException());
   }

   @Test
   public void testMethodNotSupportedException() throws Exception
   {
      doTest(new ResteasyMethodNotSupportedException(), new MethodNotSupportedException(""));
   }

   @Test
   public void testNoHttpResponseException() throws Exception
   {
      doTest(new ResteasyNoHttpResponseException(), new NoHttpResponseException(""));
   }

   @Test
   public void testNonRepeatableRequestException() throws Exception
   {
      doTest(new ResteasyNonRepeatableRequestException(), new NonRepeatableRequestException());
   }

   @Test
   public void testNTLMEngineException() throws Exception
   {
      doTest(new ResteasyNTLMEngineException(), new NTLMEngineException());
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
   public void testTunnelRefusedException() throws Exception
   {
      doTest(new ResteasyTunnelRefusedException(), new TunnelRefusedException("", null));
   }

   @Test
   public void testUnsupportedHttpVersionException() throws Exception
   {
      doTest(new ResteasyUnsupportedHttpVersionException(), new UnsupportedHttpVersionException());
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
     
   @Test
   public void testAlternativeMapperProviderInstance() throws Exception
   {
      beforeProviderInstance(new TestClientExceptionMapper());
      doTestWithAlternativeMapper(new TestException(null), new UnsupportedHttpVersionException());
   }
   
   @Test
   public void testAlternativeMapperClassName() throws Exception
   {
      beforeClassNames(TestClientExceptionMapper.class);
      doTestWithAlternativeMapper(new TestException(null), new UnsupportedHttpVersionException());
   }
   
   @Test
   public void testAlternativeMapperApplicationClasses() throws Exception
   {
      beforeApplicationClasses(TestClientExceptionMapper.class);
      doTestWithAlternativeMapper(new TestException(null), new UnsupportedHttpVersionException());
   }
   
   @Test
   public void testAlternativeMapperApplicationSingleton() throws Exception
   {
      beforeApplicationSingleton(new TestClientExceptionMapper());
      doTestWithAlternativeMapper(new TestException(null), new UnsupportedHttpVersionException());
   }
   
   @Test
   public void testAlternativeMapperContextParams() throws Exception
   {
      beforeContextParams(TestClientExceptionMapper.class);
      doTestWithAlternativeMapper(new TestException(null), new UnsupportedHttpVersionException());
   }
   
   @Test
   public void testAlternativeMapperInitParams() throws Exception
   {
      beforeInitParams(TestClientExceptionMapper.class);
      doTestWithAlternativeMapper(new TestException(null), new UnsupportedHttpVersionException());
   }
   
   private void doTestWithAlternativeMapper(Exception wrappingException, Exception embeddedException) throws Exception
   {
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
         assertTrue("Expected instance of " + wrappingException.getClass() + ", got " + t, wrappingException.getClass().equals(t.getClass()));
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
         LOG.info(indent + t);
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

      TestClientExecutor(Exception e)
      {
         this.e = e;
         if (ResteasyProviderFactory.getInstance().getClientExceptionMapper(Exception.class) == null)
         {
            Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(ApacheHttpClient4ExceptionMapper.class, ClientExceptionMapper.class)[0];
            ResteasyProviderFactory.getInstance().addClientExceptionMapper(new ApacheHttpClient4ExceptionMapper(), exceptionType);
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
      
      TestException(Exception e)
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
