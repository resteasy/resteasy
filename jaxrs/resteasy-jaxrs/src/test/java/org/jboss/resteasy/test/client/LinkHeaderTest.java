package org.jboss.resteasy.test.client;

import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.jboss.resteasy.spi.LinkHeader;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkHeaderTest extends BaseResourceTest
{
   @Path("/linkheader")
   public static class LinkHeaderService
   {
      @POST
      public Response post(@HeaderParam("Link") LinkHeader linkHeader)
      {
         System.out.println("SERVER LinkHeader: " + new LinkHeaderDelegate().toString(linkHeader));
         return Response.noContent().header("Link", linkHeader).build();
      }
      @POST
      @Path("/str")
      public Response postStr(@HeaderParam("Link") String linkHeader)
      {
         System.out.println("SERVER LINK: " + linkHeader);
         return Response.noContent().header("Link", linkHeader).build();
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      addPerRequestResource(LinkHeaderService.class);
   }

   @Test
   public void testLinkheader() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://example.com/TheBook/chapter2>; rel=\"previous\";\n" +
              "         title=\"previous chapter\"");

      Assert.assertTrue(header.getLinksByTitle().containsKey("previous chapter"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("previous"));
      Assert.assertEquals(header.getLinksByTitle().get("previous chapter").getHref(), "http://example.com/TheBook/chapter2");
      System.out.println(delegate.toString(header));
      String str = delegate.toString(header);
      header = delegate.fromString(str);
      Assert.assertTrue(header.getLinksByTitle().containsKey("previous chapter"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("previous"));
      Assert.assertEquals(header.getLinksByTitle().get("previous chapter").getHref(), "http://example.com/TheBook/chapter2");

      ClientRequest request = new ClientRequest(generateURL("/linkheader/str"));
      request.addLink("previous chapter", "previous", "http://example.com/TheBook/chapter2", null);
      ClientResponse response = request.post();
      header = response.getLinkHeader();
      Assert.assertNotNull(header);
      Assert.assertTrue(header.getLinksByTitle().containsKey("previous chapter"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("previous"));
      Assert.assertEquals(header.getLinksByTitle().get("previous chapter").getHref(), "http://example.com/TheBook/chapter2");
   }

   @Test
   public void testLinkheader2() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://example.org/>; rel=index;\n" +
              "             rel=\"start http://example.net/relation/other\"");
      Assert.assertTrue(header.getLinksByRelationship().containsKey("index"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("start"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
      System.out.println(delegate.toString(header));
      String str = delegate.toString(header);
      header = delegate.fromString(str);
      Assert.assertTrue(header.getLinksByRelationship().containsKey("index"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("start"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
      ClientRequest request = new ClientRequest(generateURL("/linkheader"));
      request.header("link", "<http://example.org/>; rel=index;" +
              "             rel=\"start http://example.net/relation/other\"");
      ClientResponse response = request.post();
      header = response.getLinkHeader();
      Assert.assertNotNull(header);
      Assert.assertTrue(header.getLinksByRelationship().containsKey("index"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("start"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
   }
}
