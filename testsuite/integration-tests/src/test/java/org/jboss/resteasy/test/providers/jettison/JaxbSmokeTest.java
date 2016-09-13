package org.jboss.resteasy.test.providers.jettison;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jaxb.json.BadgerContext;
import org.jboss.resteasy.plugins.providers.jaxb.json.JettisonMappedContext;
import org.jboss.resteasy.test.providers.jettison.resource.Book;
import org.jboss.resteasy.test.providers.jettison.resource.BookStoreClient;
import org.jboss.resteasy.test.providers.jettison.resource.BookStoreResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxbSmokeTest {

    private final Logger log = Logger.getLogger(JaxbSmokeTest.class.getName());
    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxbSmokeTest.class.getSimpleName());
        war.addClass(Book.class);
        return TestUtil.finishContainerPrepare(war, null, BookStoreResource.class);
    }

    @Before
    public void before() throws Exception {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxbSmokeTest.class.getSimpleName());
    }

    @Test
    public void testNoDefaultsResource() {

        ResteasyWebTarget target = client.target(generateURL(""));
        BookStoreClient bookStoreClient = target.proxy(BookStoreClient.class);

        Book book = bookStoreClient.getBookByISBN("596529260");
        Assert.assertNotNull(book);
        Assert.assertEquals("RESTful Web Services", book.getTitle());

        book = new Book("Bill Burke", "666", "EJB 3.0");
        bookStoreClient.addBook(book);
        book = new Book("Bill Burke", "3434", "JBoss Workbook");
        bookStoreClient.addBookJson(book);

        book = bookStoreClient.getBookByISBN("666");
        Assert.assertEquals("Bill Burke", book.getAuthor());
        Assert.assertEquals("EJB 3.0", book.getTitle());

        book = bookStoreClient.getBookByISBNJson("3434");
        Assert.assertEquals("Bill Burke", book.getAuthor());
        Assert.assertEquals("JBoss Workbook", book.getTitle());

    }

    @XmlRootElement
    public static class Library {
        private String name;
        private List<Book> books;

        @XmlAttribute
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElement(name = "registered-books")
        public List<Book> getBooks() {
            return books;
        }

        public void setBooks(List<Book> books) {
            this.books = books;
        }
    }

    @Test
    @Mapped(attributesAsElements = {"title"})
    public void testJSON() throws Exception {
        {
            Mapped mapped = JaxbSmokeTest.class.getMethod("testJSON").getAnnotation(Mapped.class);
            JettisonMappedContext context = new JettisonMappedContext(mapped, Book.class);
            StringWriter writer = new StringWriter();
            context.createMarshaller().marshal(new Book("Bill Burke", "666", "EJB 3.0"), writer);

            String val = writer.toString();
            log.info("Mapped: " + val);

            // test Mapped attributeAsElement
            Assert.assertTrue(!val.contains("@title"));
        }
        {
            BadgerContext context = new BadgerContext(Book.class);
            StringWriter writer = new StringWriter();
            context.createMarshaller().marshal(new Book("Bill Burke", "666", "EJB 3.0"), writer);
            log.info("Badger: " + writer.toString());
        }
        Library library = new Library();
        List<Book> books = new ArrayList<>();
        books.add(new Book("Bill Burke", "555", "JBoss Workbook"));
        books.add(new Book("Bill Burke", "666", "EJB 3.0"));
        library.setName("BPL");
        library.setBooks(books);

        {
            BadgerContext context = new BadgerContext(Library.class);
            StringWriter writer = new StringWriter();
            context.createMarshaller().marshal(library, writer);

            String s = writer.toString();
            log.info("Badger: " + s);
            Library lib = (Library) context.createUnmarshaller().unmarshal(new StringReader(s));
            Assert.assertEquals(lib.getName(), "BPL");
            Assert.assertEquals(lib.getBooks().size(), 2);
        }
        {
            JettisonMappedContext context = new JettisonMappedContext(Library.class);
            StringWriter writer = new StringWriter();
            context.createMarshaller().marshal(library, writer);

            String s = writer.toString();
            log.info("Mapped: " + s);
            Library lib = (Library) context.createUnmarshaller().unmarshal(new StringReader(s));
            Assert.assertEquals(lib.getName(), "BPL");
            Assert.assertEquals(lib.getBooks().size(), 2);
        }
    }

}