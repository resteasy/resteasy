package org.resteasy.test.regression;

import Acme.Serve.Serve;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.test.TJWSServletContainer;
import org.resteasy.test.xml.Book;
import org.resteasy.test.xml.BookStore;
import org.resteasy.test.xml.BookStoreClient;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSChunkTest
{
   private static Serve server = null;
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = TJWSServletContainer.start();
   }

   @AfterClass
   public static void after() throws Exception
   {
      TJWSServletContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(BookStore.class);

      HttpClient httpClient = new HttpClient();
      BookStoreClient client = ProxyFactory.create(BookStoreClient.class, "http://localhost:8081", httpClient);

      Book book = client.getBookByISBN("596529260");
      Assert.assertNotNull(book);
      Assert.assertEquals("RESTful Web Services", book.getTitle());

      // TJWS does not support chunk encodings well so I need to kill kept alive connections
      // this is a test for it, the test is fail with a put or post, then try with another invocation.  Origginally
      // the server would hang.
      //httpClient.getHttpConnectionManager().closeIdleConnections(0);

      book = new Book("Bill Burke", "666", "EJB 3.0");
      client.addBook(book);
      {
         PutMethod method = new PutMethod("http://localhost:8081/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = httpClient.executeMethod(method);
      }
      // TJWS does not support chunk encodings so I need to kill kept alive connections
      //httpClient.getHttpConnectionManager().closeIdleConnections(0);
      book = client.getBookByISBN("666");
      Assert.assertEquals("Bill Burke", book.getAuthor());
      //httpClient.getHttpConnectionManager().closeIdleConnections(0);
   }
}
