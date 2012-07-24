package org.jboss.resteasy.test.xml;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.jaxb.json.BadgerContext;
import org.jboss.resteasy.plugins.providers.jaxb.json.JettisonMappedContext;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestJAXB
{

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
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

      HttpClient httpClient = new DefaultHttpClient();
      ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
      BookStoreClient client = ProxyFactory.create(BookStoreClient.class, generateBaseUrl(),
              executor);

      Book book = client.getBookByISBN("596529260");
      Assert.assertNotNull(book);
      Assert.assertEquals("RESTful Web Services", book.getTitle());

      // TJWS does not support chunk encodings well so I need to kill kept
      // alive connections
      httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);

      book = new Book("Bill Burke", "666", "EJB 3.0");
      client.addBook(book);
      book = new Book("Bill Burke", "3434", "JBoss Workbook");
      client.addBookJson(book);
      // TJWS does not support chunk encodings so I need to kill kept alive
      // connections
      httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
      book = client.getBookByISBN("666");
      Assert.assertEquals("Bill Burke", book.getAuthor());
      book = client.getBookByISBNJson("3434");
      Assert.assertEquals("Bill Burke", book.getAuthor());
      Assert.assertEquals("JBoss Workbook", book.getTitle());
      httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
   }

   @XmlRootElement
   public static class Library
   {
      private String name;
      private List<Book> books;

      @XmlAttribute
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      @XmlElement(name = "registered-books")
      public List<Book> getBooks()
      {
         return books;
      }

      public void setBooks(List<Book> books)
      {
         this.books = books;
      }
   }

   @Test
   @Mapped(attributesAsElements = {"title"})
   public void testJSON() throws Exception
   {
      {
         Mapped mapped = TestJAXB.class.getMethod("testJSON").getAnnotation(Mapped.class);
         JettisonMappedContext context = new JettisonMappedContext(mapped, Book.class);
         StringWriter writer = new StringWriter();
         context.createMarshaller().marshal(new Book("Bill Burke", "666", "EJB 3.0"), writer);
         System.out.println("Mapped: ");
         String val = writer.toString();
         System.out.println(val);

         // test Mapped attributeAsElement
         Assert.assertTrue(val.indexOf("@title") == -1);
      }
      {
         BadgerContext context = new BadgerContext(Book.class);
         StringWriter writer = new StringWriter();
         context.createMarshaller().marshal(new Book("Bill Burke", "666", "EJB 3.0"), writer);
         System.out.println("Badger: ");
         System.out.println(writer.toString());
      }
      Library library = new Library();
      ArrayList<Book> books = new ArrayList<Book>();
      books.add(new Book("Bill Burke", "555", "JBoss Workbook"));
      books.add(new Book("Bill Burke", "666", "EJB 3.0"));
      library.setName("BPL");
      library.setBooks(books);

      {
         BadgerContext context = new BadgerContext(Library.class);
         StringWriter writer = new StringWriter();
         context.createMarshaller().marshal(library, writer);
         System.out.println("Badger: ");
         String s = writer.toString();
         System.out.println(s);
         Library lib = (Library) context.createUnmarshaller().unmarshal(new StringReader(s));
         Assert.assertEquals(lib.getName(), "BPL");
         Assert.assertEquals(lib.getBooks().size(), 2);
      }
      {
         JettisonMappedContext context = new JettisonMappedContext(Library.class);
         StringWriter writer = new StringWriter();
         context.createMarshaller().marshal(library, writer);
         System.out.println("Mapped: ");
         String s = writer.toString();
         System.out.println(s);
         Library lib = (Library) context.createUnmarshaller().unmarshal(new StringReader(s));
         Assert.assertEquals(lib.getName(), "BPL");
         Assert.assertEquals(lib.getBooks().size(), 2);
      }
   }

}