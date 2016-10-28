package org.jboss.resteasy.test.xxe;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.xxe.resource.xxeNamespace.FavoriteMovie;
import org.jboss.resteasy.test.xxe.resource.xxeNamespace.FavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.xxe.resource.xxeNamespace.FavoriteMovieXmlType;
import org.jboss.resteasy.test.xxe.resource.xxeNamespace.MovieResource;
import org.jboss.resteasy.test.xxe.resource.xxeNamespace.ObjectFactory;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter XXE
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-996
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NamespaceJaxbTest {
    protected static ResteasyClient client;
    protected static final String WRONG_RESPONSE_ERROR_MSG = "Response has wrong content";
    @Deployment
    public static Archive<?> deployDefault() {
        WebArchive war = TestUtil.prepareArchive(NamespaceJaxbTest.class.getSimpleName());
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.document.expand.entity.references", "false");
        war.addClasses(FavoriteMovie.class, FavoriteMovieXmlRootElement.class, FavoriteMovieXmlType.class, ObjectFactory.class);
        return TestUtil.finishContainerPrepare(war, contextParam, MovieResource.class);
    }

    @Before
    public void init() throws Exception {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, NamespaceJaxbTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check XML root element
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElement() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/xmlRootElement"));
        FavoriteMovieXmlRootElement movie = new FavoriteMovieXmlRootElement();
        movie.setTitle("La Regle du Jeu");
        Response response = target.request().post(Entity.entity(movie, "application/xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "La Regle du Jeu", entity);
    }

    /**
     * @tpTestDetails Check XML type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlType() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/xmlType"));
        FavoriteMovieXmlType movie = new FavoriteMovieXmlType();
        movie.setTitle("La Cage Aux Folles");
        Response response = target.request().post(Entity.entity(movie, "application/xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "La Cage Aux Folles", entity);
    }

    /**
     * @tpTestDetails Check JAXB element
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJAXBElement() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/JAXBElement"));
        String str = "<?xml version=\"1.0\"?>\r" +
                "<favoriteMovieXmlType xmlns=\"http://abc.com\"><title>La Cage Aux Folles</title></favoriteMovieXmlType>";
        Response response = target.request().post(Entity.entity(str, "application/xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "La Cage Aux Folles", entity);
    }

    /**
     * @tpTestDetails Check list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testList() throws Exception {
        doCollectionTest("list");
    }

    /**
     * @tpTestDetails Check set
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSet() throws Exception {
        doCollectionTest("set");
    }

    /**
     * @tpTestDetails Check array
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testArray() throws Exception {
        doCollectionTest("array");
    }

    /**
     * @tpTestDetails Check map
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMap() throws Exception {
        doMapTest();
    }

    void doCollectionTest(String path) throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/" + path));
        String str = "<?xml version=\"1.0\"?>\r" +
                "<collection>" +
                "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                "<favoriteMovieXmlRootElement><title>La Regle du Jeu</title></favoriteMovieXmlRootElement>" +
                "</collection>";
        Response response = target.request().post(Entity.entity(str, "application/xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        if (entity.indexOf("Cage") < entity.indexOf("Regle")) {
            Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "/La Cage Aux Folles/La Regle du Jeu", entity);
        } else {
            Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "/La Regle du Jeu/La Cage Aux Folles", entity);
        }
    }

    void doMapTest() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/map"));
        String str = "<?xml version=\"1.0\"?>\r" +
                "<map>" +
                "<entry key=\"new\">" +
                "<favoriteMovieXmlRootElement><title>La Cage Aux Folles</title></favoriteMovieXmlRootElement>" +
                "</entry>" +
                "<entry key=\"old\">" +
                "<favoriteMovieXmlRootElement><title>La Regle du Jeu</title></favoriteMovieXmlRootElement>" +
                "</entry>" +
                "</map>";
        Response response = target.request().post(Entity.entity(str, "application/xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        boolean result = false;
        if ("/La Cage Aux Folles/La Regle du Jeu".equals(entity)) {
            result = true;
        } else if ("/La Regle du Jeu/La Cage Aux Folles".equals(entity)) {
            result = true;
        }

        Assert.assertTrue(WRONG_RESPONSE_ERROR_MSG, result);
    }
}
