package org.jboss.resteasy.test.cdi.interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBook;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBookReader;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBookReaderInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBookReaderInterceptorInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBookWriter;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBookWriterInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorBookWriterInterceptorInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorClassBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorClassInterceptorStereotype;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorClassMethodInterceptorStereotype;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorFilterBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorFour;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorLifecycleBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorMethodBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorOne;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorPostConstructInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorPreDestroyInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorReaderBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorRequestFilter;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorRequestFilterInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorRequestFilterInterceptorBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorResource;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorResponseFilter;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorResponseFilterInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorResponseFilterInterceptorBinding;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorStereotyped;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorThree;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorTwo;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorVisitList;
import org.jboss.resteasy.test.cdi.interceptors.resource.InterceptorWriterBinding;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.text.Utilities;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Interceptors test.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InterceptorTest {
    protected static final Logger log = LogManager.getLogger(InterceptorTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {

        WebArchive war = TestUtil.prepareArchive(InterceptorTest.class.getSimpleName());
        war.addClasses(Constants.class, UtilityProducer.class, Utilities.class, InterceptorVisitList.class)
                .addClasses(InterceptorResource.class, InterceptorOne.class, InterceptorTwo.class)
                .addClasses(InterceptorClassBinding.class, InterceptorMethodBinding.class, InterceptorThree.class, InterceptorFour.class)
                .addClasses(InterceptorFilterBinding.class, InterceptorRequestFilterInterceptorBinding.class)
                .addClasses(InterceptorResponseFilterInterceptorBinding.class)
                .addClasses(InterceptorRequestFilterInterceptor.class, InterceptorResponseFilterInterceptor.class, InterceptorRequestFilter.class, InterceptorResponseFilter.class)
                .addClasses(InterceptorReaderBinding.class, InterceptorWriterBinding.class)
                .addClasses(InterceptorBook.class, InterceptorBookReader.class, InterceptorBookWriter.class)
                .addClasses(InterceptorBookReaderInterceptor.class, InterceptorBookWriterInterceptor.class)
                .addClasses(InterceptorBookReaderInterceptorInterceptor.class, InterceptorBookWriterInterceptorInterceptor.class)
                .addClasses(InterceptorClassInterceptorStereotype.class, InterceptorClassMethodInterceptorStereotype.class, InterceptorStereotyped.class)
                .addClasses(InterceptorLifecycleBinding.class, InterceptorPostConstructInterceptor.class, InterceptorPreDestroyInterceptor.class)
                .addAsWebInfResource(InterceptorTest.class.getPackage(), "interceptorBeans.xml", "beans.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InterceptorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails One item is stored and load to collection in resources.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInterceptors() throws Exception {
        Client client = ClientBuilder.newClient();

        // Create book.
        InterceptorBook book = new InterceptorBook("RESTEasy: the Sequel");
        WebTarget base = client.target(generateURL("/create/"));
        Response response = base.request().post(Entity.entity(book, Constants.MEDIA_TYPE_TEST_XML));
        assertEquals(200, response.getStatus());
        int id = response.readEntity(int.class);
        assertThat("Id of stored book is wrong.", 0, is(id));

        // Retrieve book.
        base = client.target(generateURL("/book/" + id));
        response = base.request().accept(Constants.MEDIA_TYPE_TEST_XML).get();
        assertEquals(200, response.getStatus());
        InterceptorBook result = response.readEntity(InterceptorBook.class);
        assertEquals("Wrong book is received.", book, result);

        // check interceptors
        base = client.target(generateURL("/test/"));
        response = base.request().post(Entity.text(new String()));
        assertEquals(200, response.getStatus());

        client.close();
    }
}
