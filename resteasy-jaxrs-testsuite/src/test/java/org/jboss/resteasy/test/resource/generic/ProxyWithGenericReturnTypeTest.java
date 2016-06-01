package org.jboss.resteasy.test.resource.generic;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="holger.morch@navteq.com">Holger Morch</a>
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 28, 2012
 */
public class ProxyWithGenericReturnTypeTest
{
   protected ResteasyDeployment deployment;

   public interface TestSubResourceIntf<T>
   {
      @GET
      @Path("list")
      @Produces("text/plain")
      public List<T> resourceMethod();
   }
   public interface TestSubResourceSubIntf extends TestSubResourceIntf<String>
   {
   }
   
   static class TestInvocationHandler implements InvocationHandler
   {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         System.out.println("entered proxied subresource");
         System.out.println("generic return type: " + method.getGenericReturnType());
         System.out.println("type of return type: " + method.getGenericReturnType().getClass());
         List<String> result = new ArrayList<String>();
         return result;
      }
   }
   
   @Provider
   static class TestMessageBodyWriter implements MessageBodyWriter<List<String>>
   {
      @Override
      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return true;
      }

      @Override
      public long getSize(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      @Override
      public void writeTo(List<String> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException
      {
         String val = "null";
         if (genericType == null) val = "null";
         else if (genericType instanceof ParameterizedType)
         {
            ParameterizedType parameterizedType = (ParameterizedType)genericType;
            val = ((Class)parameterizedType.getRawType()).getSimpleName() + "<";
            Type paramType = parameterizedType.getActualTypeArguments()[0];
            if (paramType instanceof Class) val += ((Class)paramType).getSimpleName();
            else val += paramType.toString();
            val += ">";
         }
         else if (genericType instanceof TypeVariable) val = "TypeVariable";
         else if (genericType instanceof GenericArrayType) val = "GenericArrayType";
         else val = "Type";

         entityStream.write(val.getBytes());
      }
   }
   
   static public class TestApplication extends Application
   {
      public Set<Class<?>> getClasses()
      {
         Set<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
      public Set<Object> getSingletons()
      {
         Set<Object> singletons = new HashSet<Object>();
         singletons.add(new TestMessageBodyWriter());
         return singletons;
      }
   }

   @Path("/")
   static public class TestResource
   {  
      @Produces("text/plain")
      @Path("test")
      public TestSubResourceSubIntf resourceLocator()
      {
         Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                               new Class[]{TestSubResourceSubIntf.class}, 
                                               new TestInvocationHandler());

         return TestSubResourceSubIntf.class.cast(proxy);
      }
   }

   @Before
   public void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<String,String>();
      Hashtable<String,String> contextParams = new Hashtable<String,String>();
      contextParams.put("javax.ws.rs.Application", TestApplication.class.getName());
      deployment = EmbeddedContainer.start(initParams, contextParams);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void test() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8081/test/list/");
      System.out.println("Sending request");
      ClientResponse<String>response = request.get(String.class);
      System.out.println("Received response: " + response.getEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity(String.class).indexOf("List<String>") >= 0);
   }   
}