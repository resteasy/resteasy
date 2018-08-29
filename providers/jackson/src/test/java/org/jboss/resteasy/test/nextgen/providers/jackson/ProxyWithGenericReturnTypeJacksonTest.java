package org.jboss.resteasy.test.nextgen.providers.jackson;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="holger.morch@navteq.com">Holger Morch</a>
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 28, 2012
 */
public class ProxyWithGenericReturnTypeJacksonTest
{
    private static final Logger LOG = Logger.getLogger(ProxyWithGenericReturnTypeJacksonTest.class);
   protected ResteasyDeployment deployment;
   
   @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
   @JsonSubTypes({
           @JsonSubTypes.Type(value = Type1.class, name = "type1"),
           @JsonSubTypes.Type(value = Type2.class, name = "type2")})
   public abstract static class AbstractParent {
       
       protected long id;
   
       public long getId() {
           return id;
       }
   
       public void setId(long id) {
           this.id = id;
       }
   }
   
   public static class Type1 extends AbstractParent {
   
       protected String name;
   
       public String getName() {
           return name;
       }
   
       public void setName(String name) {
           this.name = name;
       }
   }
   
   public static class Type2 extends AbstractParent {
   
       protected String note;
   
       public String getNote() {
           return note;
       }
   
       public void setNote(String note) {
           this.note = note;
       }
   }
   
   public interface TestSubResourceIntf
   {
      @GET
      @Path("list")
      @Produces("application/*+json")
      List<AbstractParent> resourceMethod();

      @GET
      @Path("one")
      @Produces("application/*+json")
      AbstractParent resourceMethodOne();
   }
   
   public interface TestSubResourceSubIntf extends TestSubResourceIntf
   {
   }
   
   static class TestInvocationHandler implements InvocationHandler
   {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         LOG.info("entered proxied subresource");
         LOG.info("method: " + method.getName());
         LOG.info("generic return type: " + method.getGenericReturnType());
         LOG.info("type of return type: " + method.getGenericReturnType().getClass());
         if ("resourceMethod".equals(method.getName())) {
             List<AbstractParent> l = new ArrayList<AbstractParent>();
             Type1 first = new Type1();
             first.setId(1);
             first.setName("MyName");
             l.add(first);
             
             Type2 second = new Type2();
             second.setId(2);
             second.setNote("MyNote");
             l.add(second);
             return l;
         }
         
         if ("resourceMethodOne".equals(method.getName())) {
             Type1 first = new Type1();
             first.setId(1);
             first.setName("MyName");
             return first;
         }
         
         return null;
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
       ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget target = client.target("http://localhost:8081/test/one/");
        LOG.info("Sending request");
        Response response = target.request().get();
       String entity = response.readEntity(String.class);
       LOG.info("Received response: " + entity);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue("Type property is missing.", entity.contains("type"));
       response.close();

       target = client.target("http://localhost:8081/test/list/");
        LOG.info("Sending request");
        response = target.request().get();
        entity = response.readEntity(String.class);
       LOG.info("Received response: " + entity);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue("Type property is missing.", entity.contains("type"));
       response.close();
       client.close();
    }
}