package org.jboss.resteasy.test.providers.atom;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.plugins.providers.atom.AbderaEntryProvider;
import org.jboss.resteasy.plugins.providers.atom.AbderaFeedProvider;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbderaTest extends BaseResourceTest
{

   @Path("atom")
   public static class MyResource
   {
      private static final Abdera abdera = new Abdera();

      @GET
      @Path("feed")
      @Produces(MediaType.APPLICATION_ATOM_XML)
      public Feed getFeed(@Context UriInfo uri) throws Exception
      {
         Factory factory = abdera.getFactory();
         Assert.assertNotNull(factory);
         Feed feed = abdera.getFactory().newFeed();
         feed.setId("tag:example.org,2007:/foo");
         feed.setTitle("Test Feed");
         feed.setSubtitle("Feed subtitle");
         feed.setUpdated(new Date());
         feed.addAuthor("James Snell");
         feed.addLink("http://example.com");

         Entry entry = feed.addEntry();
         entry.setId("tag:example.org,2007:/foo/entries/1");
         entry.setTitle("Entry title");
         entry.setUpdated(new Date());
         entry.setPublished(new Date());
         entry.addLink(uri.getRequestUri().toString());

         Customer cust = new Customer("bill");

         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         StringWriter writer = new StringWriter();
         ctx.createMarshaller().marshal(cust, writer);
         entry.setContent(writer.toString(), "application/xml");
         return feed;

      }

      @PUT
      @Path("feed")
      @Consumes(MediaType.APPLICATION_ATOM_XML)
      public void putFeed(Feed feed) throws Exception
      {
         String content = feed.getEntries().get(0).getContent();
         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         Customer cust = (Customer) ctx.createUnmarshaller().unmarshal(new StringReader(content));
         Assert.assertEquals("bill", cust.getName());

      }

      @GET
      @Path("entry")
      @Produces(MediaType.APPLICATION_ATOM_XML)
      public Entry getEntry(@Context UriInfo uri) throws Exception
      {
         Entry entry = abdera.getFactory().newEntry();
         entry.setId("tag:example.org,2007:/foo/entries/1");
         entry.setTitle("Entry title");
         entry.setUpdated(new Date());
         entry.setPublished(new Date());
         entry.addLink(uri.getRequestUri().toString());

         Customer cust = new Customer("bill");

         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         StringWriter writer = new StringWriter();
         ctx.createMarshaller().marshal(cust, writer);
         entry.setContent(writer.toString(), "application/xml");
         return entry;

      }

      @PUT
      @Path("entry")
      @Consumes(MediaType.APPLICATION_ATOM_XML)
      public void putFeed(Entry entry) throws Exception
      {
         String content = entry.getContent();
         JAXBContext ctx = JAXBContext.newInstance(Customer.class);
         Customer cust = (Customer) ctx.createUnmarshaller().unmarshal(new StringReader(content));
         Assert.assertEquals("bill", cust.getName());

      }
   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getProviderFactory().registerProvider(AbderaFeedProvider.class);
      dispatcher.getProviderFactory().registerProvider(AbderaEntryProvider.class);
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
   }

   @Test
   public void testAbderaFeed() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/atom/feed");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      String str = method.getResponseBodyAsString();

      PutMethod put = createPutMethod("/atom/feed");
      put.setRequestEntity(new StringRequestEntity(str, MediaType.APPLICATION_ATOM_XML, null));
      status = client.executeMethod(put);
      Assert.assertEquals(200, status);

   }

   @Test
   public void testAbderaEntry() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/atom/entry");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      String str = method.getResponseBodyAsString();

      PutMethod put = createPutMethod("/atom/entry");
      put.setRequestEntity(new StringRequestEntity(str, MediaType.APPLICATION_ATOM_XML, null));
      status = client.executeMethod(put);
      Assert.assertEquals(200, status);
   }
}
