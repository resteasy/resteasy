package org.jboss.resteasy.test;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 09 26 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class MultipleInterfaceProxyTest {

    protected ResteasyDeployment deployment;

      public interface Intf1
      {
         @GET
         @Produces("text/plain")
         @Path("hello1")
         public String resourceMethod1();
      }

      public interface Intf2
      {
         @GET
         @Produces("text/plain")
         @Path("hello2")
         public String resourceMethod2();
      }

      @Path("/")
      static public class TestResource
      {
         @Produces("text/plain")
         @Path("test")
         public Object resourceLocator()
         {
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{Intf1.class, Intf2.class},
                    new TestInvocationHandler());
            return proxy;
         }
      }

      static class TestInvocationHandler implements InvocationHandler
      {
         @Override
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
         {
            System.out.println("entered proxied subresource");
            return method.getName();
         }
      }

      @Before
      public void before() throws Exception
      {
         deployment = EmbeddedContainer.start();
         deployment.getRegistry().addPerRequestResource(TestResource.class);
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
         ClientRequest request = new ClientRequest("http://localhost:8081/test/hello1/");
         ClientResponse<String> response = request.get(String.class);
         System.out.println("Received first response: " + response.getEntity());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("resourceMethod1", response.getEntity());

         request = new ClientRequest("http://localhost:8081/test/hello2/");
         response = request.get(String.class);
         System.out.println("Received second response: " + response.getEntity());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("resourceMethod2", response.getEntity());
      }
}