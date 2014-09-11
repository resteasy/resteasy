package org.jboss.resteasy.test.nextgen.finegrain;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.delegates.DateDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.DateUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-915
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 9, 2014
 */
public class HeaderDelegateTest extends BaseResourceTest
{
   private static final Date RIGHT_AFTER_BIG_BANG = new TestDate(3000);
   
   public static class TestDate extends Date
   {
      private static final long serialVersionUID = 1L;
      
      public TestDate(long date)
      {
         super(date);
      }
   }
   
   public interface i1 {}
   public interface i2 extends i1 {}
   public interface i3 extends i1 {}
   public interface i4 extends i3 {}
   
   public static class TestDelegate<T> implements i4, HeaderDelegate<T>
   {
      @Override
      public T fromString(String value)
      {
         return null;
      }

      @Override
      public String toString(T value)
      {
         return null;
      }
   }
   
   public static class SubDelegate<T> extends TestDelegate<T>
   {
   }
   
   @Path("/last")
   public static class TestResource
   {
      @GET
      @Produces("text/plain")
      public Response last()
      {
         return Response.ok().lastModified(RIGHT_AFTER_BIG_BANG).build();
      }
   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @Test
   public void lastModifiedTest() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/last"));
      Invocation.Builder request = target.request();
      Response response = request.get();
      System.out.println("status: " + response.getStatus());
      System.out.println("lastModified string: " + response.getHeaderString("last-modified"));
      Date last = response.getLastModified();
      System.out.println("lastModified Date:   " + DateUtil.formatDate(last));
      System.out.println("expected:            " + DateUtil.formatDate(RIGHT_AFTER_BIG_BANG));
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(DateUtil.formatDate(RIGHT_AFTER_BIG_BANG), DateUtil.formatDate(last));
      client.close();
   }
   
   @Test
   public void localTest() throws Exception
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      Assert.assertEquals(DateDelegate.class, factory.getHeaderDelegate(TestDate.class).getClass());
      Assert.assertEquals(DateDelegate.class, factory.createHeaderDelegate(TestDate.class).getClass());
      
      @SuppressWarnings("rawtypes")
      SubDelegate<?> delegate = new SubDelegate();
      factory.addHeaderDelegate(i1.class, delegate);
      Assert.assertEquals(delegate, factory.getHeaderDelegate(i1.class));
      Assert.assertEquals(delegate, factory.getHeaderDelegate(i2.class));
      Assert.assertEquals(delegate, factory.getHeaderDelegate(i3.class));
      Assert.assertEquals(delegate, factory.getHeaderDelegate(i4.class));
      Assert.assertEquals(delegate, factory.getHeaderDelegate(TestDelegate.class));
      Assert.assertEquals(delegate, factory.getHeaderDelegate(SubDelegate.class));
   }
}
