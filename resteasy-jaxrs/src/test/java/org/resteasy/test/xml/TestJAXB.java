package org.resteasy.test.xml;

import Acme.Serve.Serve;
import org.apache.commons.httpclient.HttpClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.test.EmbeddedContainer;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestJAXB
{

   private static Serve server = null;
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      POJOResourceFactory noDefaults = new POJOResourceFactory(BookStore.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      HttpClient httpClient = new HttpClient();
      BookStoreClient client = ProxyFactory.create(BookStoreClient.class, "http://localhost:8081", httpClient);

      Book book = client.getBookByISBN("596529260");
      Assert.assertNotNull(book);
      Assert.assertEquals("RESTful Web Services", book.getTitle());

      // TJWS does not support chunk encodings well so I need to kill kept alive connections
      httpClient.getHttpConnectionManager().closeIdleConnections(0);

      book = new Book("Bill Burke", "666", "EJB 3.0");
      client.addBook(book);
      // TJWS does not support chunk encodings so I need to kill kept alive connections
      httpClient.getHttpConnectionManager().closeIdleConnections(0);
      book = client.getBookByISBN("666");
      Assert.assertEquals("Bill Burke", book.getAuthor());
      httpClient.getHttpConnectionManager().closeIdleConnections(0);
   }


}