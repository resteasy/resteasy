package org.jboss.restasy.test.providers.atom;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceTest extends BaseResourceTest
{
   @Path("atom")
   public static class AtomServer
   {
      @GET
      @Path("entry")
      @Produces("application/atom+xml")
      public Entry getEntry()
      {
         Entry entry = new Entry();
         entry.setTitle("Hello World");
         Content content = new Content();
         content.setJAXBObject(new Customer("bill"));
         entry.setContent(content);
         return entry;
      }

      @GET
      @Path("feed")
      @Produces("application/atom+xml")
      public Feed getFeed()
      {
         Feed feed = new Feed();
         feed.getEntries().add(getEntry());
         return feed;
      }
   }


   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(AtomServer.class);
   }

   @Test
   public void testAtomFeed() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod("http://localhost:8081/atom/feed");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      System.out.println(get.getResponseBodyAsString());
   }

   @Test
   public void testAtomEntry() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod("http://localhost:8081/atom/entry");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
      System.out.println(get.getResponseBodyAsString());
   }
}
