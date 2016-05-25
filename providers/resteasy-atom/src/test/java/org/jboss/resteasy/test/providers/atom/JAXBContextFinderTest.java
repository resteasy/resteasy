package org.jboss.resteasy.test.providers.atom;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 13, 2015
 */
public class JAXBContextFinderTest extends BaseResourceTest
{
   @Path("atom")
   public static class AtomServer
   {
      @GET
      @Path("feed")
      @Produces("application/atom+xml")
      public Feed getFeed() throws URISyntaxException
      {
         Feed feed = new Feed();
         feed.setTitle("My Feed");
         Entry entry = new Entry();
         entry.setTitle("Hello World");
         entry.setAnyOtherJAXBObject(new CustomerAtom("bill"));
         feed.getEntries().add(entry);
         entry = new Entry();
         entry.setTitle("Hello Uranus");
         entry.setAnyOtherJAXBObject(new CustomerAtom("bob"));
         feed.getEntries().add(entry);
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
      ClientRequest request = new ClientRequest(generateURL("/atom/feed"));
      ClientResponse<Feed> response = request.get(Feed.class);
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.getEntity());
      Feed feed = response.getEntity();
      Iterator<Entry> it = feed.getEntries().iterator();
      Entry entry1 = it.next();
      Entry entry2 = it.next();
      Field field = Entry.class.getDeclaredField("finder");
      field.setAccessible(true);
      Assert.assertNotNull(field.get(entry1));
      Assert.assertEquals(field.get(entry1), field.get(entry2));
   }
}
