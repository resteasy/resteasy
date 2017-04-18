package org.jboss.resteasy.test.cdi.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookReader;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookReaderDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookReaderInterceptor;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookReaderInterceptorDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookWriter;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookWriterDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookWriterInterceptor;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsBookWriterInterceptorDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResource;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResourceDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.EJBBook;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsFilterBinding;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsRequestFilterDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResourceBinding;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResourceInterceptor;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResponseFilterDecorator;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsTestRequestFilter;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResourceIntf;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsResponseFilter;
import org.jboss.resteasy.test.cdi.basic.resource.DecoratorsVisitList;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Utilities;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for integration of RESTEasy and CDI decorators.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DecoratorsTest {

    private static Logger log = Logger.getLogger(DecoratorsTest.class);

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(DecoratorsTest.class.getSimpleName())
            .addClasses(Constants.class, UtilityProducer.class, Utilities.class, DecoratorsVisitList.class, PortProviderUtil.class)
            .addClasses(DecoratorsResourceIntf.class, DecoratorsResource.class, EJBBook.class)
            .addClasses(DecoratorsBookReaderInterceptorDecorator.class, DecoratorsBookReaderInterceptor.class)
            .addClasses(DecoratorsBookReaderDecorator.class, DecoratorsBookReader.class)
            .addClasses(DecoratorsBookWriterInterceptorDecorator.class, DecoratorsBookWriterInterceptor.class)
            .addClasses(DecoratorsBookWriterDecorator.class, DecoratorsBookWriter.class)
            .addClasses(DecoratorsResourceBinding.class, DecoratorsResourceInterceptor.class, DecoratorsResourceDecorator.class)
            .addClasses(DecoratorsFilterBinding.class, DecoratorsTestRequestFilter.class, DecoratorsRequestFilterDecorator.class)
            .addClasses(DecoratorsResponseFilter.class, DecoratorsResponseFilterDecorator.class)
            .addAsWebInfResource(DecoratorsTest.class.getPackage(), "decoratorBeans.xml", "beans.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DecoratorsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Store Book to server, received it and check decorator usage.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDecorators() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        WebTarget base = client.target(generateURL("/create/"));
        EJBBook book = new EJBBook("RESTEasy: the Sequel");
        Response response = base.request().post(Entity.entity(book, Constants.MEDIA_TYPE_TEST_XML_TYPE));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        log.info("Status: " + response.getStatus());
        int id = response.readEntity(int.class);
        log.info("id: " + id);
        assertEquals("Wrong id of received book", 0, id);
        response.close();

        // Retrieve book.
        base = client.target(generateURL("/book/" + id));
        response = base.request().accept(Constants.MEDIA_TYPE_TEST_XML).get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        EJBBook result = response.readEntity(EJBBook.class);
        log.info("book: " + book);
        assertEquals("Wrong received book", book, result);
        response.close();

        // Test order of decorator invocations.
        base = client.target(generateURL("/test/"));
        response = base.request().post(Entity.text(new String()));
        assertEquals("Wrong decorator usage", HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        client.close();
    }
}
