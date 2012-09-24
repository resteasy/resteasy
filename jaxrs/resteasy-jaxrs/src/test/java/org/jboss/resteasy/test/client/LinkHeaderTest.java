package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.delegates.LinkHeaderDelegate;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.LinkHeader;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

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

      @HEAD
      @Path("/topic")
      public Response head(@Context UriInfo uriInfo)
      {
         return Response.ok()
                 .header("Link", getSenderLink(uriInfo))
                 .header("Link", getTopLink(uriInfo)).build();
      }

      protected String getSenderLink(UriInfo info)
      {
         String basePath = info.getMatchedURIs().get(0);
         UriBuilder builder = info.getBaseUriBuilder();
         builder.path(basePath);
         builder.path("sender");
         String link = "<" + builder.build().toString() + ">; rel=\"sender\"; title=\"sender\"";
         return link;
      }

      protected String getTopLink(UriInfo info)
      {
         String basePath = info.getMatchedURIs().get(0);
         UriBuilder builder = info.getBaseUriBuilder();
         builder.path(basePath);
         builder.path("poller");
         String link = "<" + builder.build().toString() + ">; rel=\"top-message\"; title=\"top-message\"";
         return link;
      }

   }

   @BeforeClass
   public static void init() throws Exception
   {
      addPerRequestResource(LinkHeaderService.class);
   }

   @Test
   public void testTopic() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://localhost:8081/linkheader/topic/sender>; rel=\"sender\"; title=\"sender\", <http://localhost:8081/linkheader/topic/poller>; rel=\"top-message\"; title=\"top-message\"");
      Link sender = header.getLinkByTitle("sender");
      Assert.assertNotNull(sender);
      Assert.assertEquals("http://localhost:8081/linkheader/topic/sender", sender.getHref());
      Assert.assertEquals("sender", sender.getRelationship());
      Link top = header.getLinkByTitle("top-message");
      Assert.assertNotNull(top);
      Assert.assertEquals("http://localhost:8081/linkheader/topic/poller", top.getHref());
      Assert.assertEquals("top-message", top.getRelationship());

   }

   @Test
   public void testTopic2() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://localhost:8081/topics/test/poller/next?index=0>; rel=\"next-message\"; title=\"next-message\",<http://localhost:8081/topics/test/poller>; rel=\"generator\"; title=\"generator\"");
      Link next = header.getLinkByTitle("next-message");
      Assert.assertNotNull(next);
      Assert.assertEquals("http://localhost:8081/topics/test/poller/next?index=0", next.getHref());
      Assert.assertEquals("next-message", next.getRelationship());
      Link generator = header.getLinkByTitle("generator");
      Assert.assertNotNull(generator);
      Assert.assertEquals("http://localhost:8081/topics/test/poller", generator.getHref());
      Assert.assertEquals("generator", generator.getRelationship());


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

   @Test
   public void testAdd()
   {
      final LinkHeader linkHeader = new LinkHeader();
      Assert.assertEquals(linkHeader.getLinks().size(), 0);
      linkHeader.addLink(new Link("one", "resl-1", "href-1", null, null));
      Assert.assertEquals(linkHeader.getLinks().size(), 1);
      linkHeader.addLink(new Link("two", "resl-2", "href-2", null, null));
      Assert.assertEquals(linkHeader.getLinks().size(), 2);
   }
}
