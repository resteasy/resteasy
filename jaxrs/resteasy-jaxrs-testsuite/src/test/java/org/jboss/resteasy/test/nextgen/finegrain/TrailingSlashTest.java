package org.jboss.resteasy.test.nextgen.finegrain;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1124
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class TrailingSlashTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("test")
   public static class TestResource
   {
      private @Context UriInfo uriInfo;

      @GET
      @Produces("text/plain")
      public Response test()
      {
         System.out.println("uriInfo: " + uriInfo.getPath());
         return Response.ok(uriInfo.getPath()).build();
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void oneArgConstructorTest() throws Exception
   {
      doOneArgConstructorTest(new URI("http://localhost/abc"), "/abc");
      doOneArgConstructorTest(new URI("http://localhost/abc/"), "/abc/");
   }

   void doOneArgConstructorTest(URI uri, String path)
   {
      ResteasyUriInfo ruri = new ResteasyUriInfo(uri);
      Assert.assertEquals(path, ruri.getPath());
      Assert.assertEquals(path, ruri.getPath(true));
      Assert.assertEquals(path, ruri.getPath(false));
      Assert.assertEquals(uri, ruri.getAbsolutePath());
      Assert.assertEquals(uri, ruri.getBaseUri().resolve(ruri.getPath(false))); 
   }
   
   //@Test
   public void twoArgConstructorTest() throws Exception
   {
      doTwoArgConstructorTest(new URI("http://localhost/abc"), new URI("xyz"), "/xyz");
      doTwoArgConstructorTest(new URI("http://localhost/abc"), new URI("xyz/"), "/xyz/");
   }
   
   void doTwoArgConstructorTest(URI base, URI relative, String path) throws URISyntaxException
   {
      ResteasyUriInfo ruri = new ResteasyUriInfo(base, relative);
      Assert.assertEquals(path, ruri.getPath());
      Assert.assertEquals(path, ruri.getPath(true));
      Assert.assertEquals(path, ruri.getPath(false));
      URI newUri;
      if (base.toString().endsWith("/"))
      {
         newUri = new URI(base.toString().substring(0, base.toString().length() - 1) + path);
      }
      else
      {
         newUri = new URI(base.toString() + path);
      }
      Assert.assertEquals(newUri, ruri.getAbsolutePath());
      Assert.assertEquals(newUri, ruri.getBaseUri().resolve(ruri.getPath(false))); 
   }

   @Test
   public void threeArgConstructorTest() throws Exception
   {
      doThreeArgConstructorTest("http://localhost/abc", "/abc");
      doThreeArgConstructorTest("http://localhost/abc/", "/abc/");
   }

   void doThreeArgConstructorTest(String s, String path) throws URISyntaxException
   {
      ResteasyUriInfo ruri = new ResteasyUriInfo(s, "", "");
      URI uri = new URI(s);
      Assert.assertEquals(path, ruri.getPath());
      Assert.assertEquals(path, ruri.getPath(true));
      Assert.assertEquals(path, ruri.getPath(false));
      Assert.assertEquals(uri, ruri.getAbsolutePath());
      Assert.assertEquals(uri, ruri.getBaseUri().resolve(ruri.getPath(false))); 
   }

   //@Test
   public void testNoSlash()
   {
      Client client = ClientBuilder.newClient();;
      WebTarget target = client.target("http://localhost:8081/test");
      Response response = target.request().get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals("/test", response.readEntity(String.class));
   }

   //@Test
   public void testSlash()
   {
      Client client = ClientBuilder.newClient();;
      WebTarget target = client.target("http://localhost:8081/test/");
      Response response = target.request().get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals("/test/", response.readEntity(String.class));
   }
}
