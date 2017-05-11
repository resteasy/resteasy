package org.jboss.resteasy.test.sourceProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.sourceProvider.resource.Book;
import org.jboss.resteasy.test.sourceProvider.resource.BookResource;
import org.jboss.resteasy.test.sourceProvider.resource.SourceProviderApp;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@RunWith(Arquillian.class)
@RunAsClient
public class SourceProviderTest {

    private static Client client;
    private static final String PATH = "http://localhost:8080/sourceProvider/test";

    String book = "<book><title>Monkey kingdom</title></book>";

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "sourceProvider.war")
            .addClasses(SourceProviderApp.class)
            .addClasses(Book.class)
            .addClasses(BookResource.class);
        System.out.println(war.toString(true));
        return war;
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    // This shows the bug
    @Test
    public void testSourceWithStringReader() throws Exception {
        try {
            Response response = client.target(PATH).request()
                .post(Entity.entity(new StreamSource(new StringReader(book)), "application/*+xml"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            Assert.assertTrue(entity.contentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><title>Monkey kingdom</title></book>"));
        } catch (Exception e) {
            e.printStackTrace();    //todo for debugging only
            throw e;
        }
    }

    // This case works correctly
    @Test
    public void testSourceWithInputStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(book.getBytes(StandardCharsets.UTF_8));
        Response response = client.target(PATH).request()
            .post(Entity.entity(new StreamSource(stream), "application/*+xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertTrue(entity.contentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><title>Monkey kingdom</title></book>"));
    }
}
