package org.jboss.resteasy.test.xxe;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.xxe.resource.xxeJettison.FavoriteMovie;
import org.jboss.resteasy.test.xxe.resource.xxeJettison.FavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.xxe.resource.xxeJettison.FavoriteMovieXmlType;
import org.jboss.resteasy.test.xxe.resource.xxeJettison.JettisonMovieResource;
import org.jboss.resteasy.test.xxe.resource.xxeJettison.MovieMap;
import org.jboss.resteasy.test.xxe.resource.xxeJettison.ObjectFactory;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @tpSubChapter XXE
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-647.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class XxeJettisonTest {

    private static final Logger log = Logger.getLogger(XxeJettisonTest.class);
    private static ResteasyClient client;

    private static final String DEFAULT = "default";
    private static final String FALSE = "false";
    private static final String TRUE = "true";

    @Deployment(name = DEFAULT)
    public static Archive<?> deployDefault() {
        return deploy(DEFAULT);
    }

    @Deployment(name = FALSE)
    public static Archive<?> deployFalse() {
        return deploy(FALSE);
    }

    @Deployment(name = TRUE)
    public static Archive<?> deplouTrue() {
        return deploy(TRUE);
    }

    @Before
    public void before() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() {
        client.close();
        client = null;
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testXmlRootElementDefault() {
        doTestXmlRootElement(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testXmlRootElementWithoutExpansion() {
        doTestXmlRootElement(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testXmlRootElementWithExpansion() {
        doTestXmlRootElement(TRUE);
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testXmlTypeDefault() {
        doTestXmlType(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testXmlTypeWithoutExpansion() {
        doTestXmlType(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testXmlTypeWithExpansion() {
        doTestXmlType(TRUE);
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testJAXBElementDefault() {
        doTestJAXBElement(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testJAXBElementWithoutExpansion() {
        doTestJAXBElement(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testJAXBElementWithExpansion() {
        doTestJAXBElement(TRUE);
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testListDefault() {
        doTestList(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testListWithoutExpansion() {
        doTestList(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testListWithExpansion() {
        doTestList(TRUE);
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testSetDefault() {
        doTestSet(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testSetWithoutExpansion() {
        doTestSet(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testSetWithExpansion() {
        doTestSet(TRUE);
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testArrayDefault() {
        doTestArray(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testArrayWithoutExpansion() {
        doTestArray(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testArrayWithExpansion() {
        doTestArray(TRUE);
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testMapDefault() {
        doTestMap(DEFAULT);
    }

    @Test
    @OperateOnDeployment(FALSE)
    public void testMapWithoutExpansion() {
        doTestMap(FALSE);
    }

    @Test
    @OperateOnDeployment(TRUE)
    public void testMapWithExpansion() {
        doTestMap(TRUE);
    }

    private void doTestXmlRootElement(String deploymentName) {
        FavoriteMovieXmlRootElement m = new FavoriteMovieXmlRootElement();
        m.setTitle("&xxe");

        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlRootElement", deploymentName));
        Response response = target.request().post(Entity.entity(m, "application/*+json"));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        log.info("Result: " + entity);
        Assert.assertTrue(!entity.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private void doTestXmlType(String deploymentName) {
        FavoriteMovieXmlType m = new FavoriteMovieXmlType();
        m.setTitle("&xxe");

        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlType", deploymentName));
        Response response = target.request().post(Entity.entity(m, "application/*+json"));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        log.info("Result: " + entity);
        Assert.assertTrue(!entity.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private void doTestJAXBElement(String deploymentName) {
        FavoriteMovieXmlType m = new FavoriteMovieXmlType();
        m.setTitle("&xxe");

        WebTarget target = client.target(PortProviderUtil.generateURL("/JAXBElement", deploymentName));
        Response response = target.request().post(Entity.entity(m, "application/*+json"));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        log.info("Result: " + entity);
        Assert.assertTrue(!entity.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private void doTestList(String deploymentName) {

        List<FavoriteMovieXmlRootElement> list = new ArrayList<>();
        FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
        m1.setTitle("&xxe");
        list.add(m1);

        FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
        m2.setTitle("Le Regle de Jeu");
        list.add(m2);

        WebTarget target = client.target(PortProviderUtil.generateURL("/list", deploymentName));

        GenericType<?> type = new GenericType<List<FavoriteMovieXmlRootElement>>() {
        };
        GenericEntity<List<FavoriteMovieXmlRootElement>> genericEntity = new GenericEntity<>(list, type.getType());
        Entity<GenericEntity<List<FavoriteMovieXmlRootElement>>> entity = Entity.entity(genericEntity, "application/*+json");

        Response response = target.request().post(entity);

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String result = response.readEntity(String.class);
        log.info("Result: " + result);
        Assert.assertTrue(!result.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private void doTestSet(String deploymentName) {

        Set<FavoriteMovieXmlRootElement> set = new HashSet<>();
        FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
        m1.setTitle("&xxe");
        set.add(m1);

        FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
        m2.setTitle("Le Regle de Jeu");
        set.add(m2);

        WebTarget target = client.target(PortProviderUtil.generateURL("/set", deploymentName));

        GenericType<?> type = new GenericType<Set<FavoriteMovieXmlRootElement>>() {
        };
        GenericEntity<Set<FavoriteMovieXmlRootElement>> genericEntity = new GenericEntity<>(set, type.getType());
        Entity<GenericEntity<Set<FavoriteMovieXmlRootElement>>> entity = Entity.entity(genericEntity, "application/*+json");

        Response response = target.request().post(entity);

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String result = response.readEntity(String.class);
        log.info("Result: " + result);
        Assert.assertTrue(!result.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private void doTestArray(String deploymentName) {

        FavoriteMovieXmlRootElement[] array = new FavoriteMovieXmlRootElement[2];
        FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
        m1.setTitle("&xxe");
        array[0] = m1;

        FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
        m2.setTitle("Le Regle de Jeu");
        array[1] = m2;

        WebTarget target = client.target(PortProviderUtil.generateURL("/array", deploymentName));
        Response response = target.request().post(Entity.entity(array, "application/*+json"));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        log.info("Result: " + entity);
        Assert.assertTrue(!entity.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private void doTestMap(String deploymentName) {

        Map<String, FavoriteMovieXmlRootElement> map = new MovieMap<>();
        FavoriteMovieXmlRootElement m1 = new FavoriteMovieXmlRootElement();
        m1.setTitle("&xxe");
        map.put("american", m1);

        FavoriteMovieXmlRootElement m2 = new FavoriteMovieXmlRootElement();
        m2.setTitle("La Regle de Jeu");
        map.put("french", m2);

        WebTarget target = client.target(PortProviderUtil.generateURL("/map", deploymentName));

        GenericType<?> type = new GenericType<Map<String, FavoriteMovieXmlRootElement>>() {
        };
        GenericEntity<Map<String, FavoriteMovieXmlRootElement>> genericEntity = new GenericEntity<>(map, type.getType());
        Entity<GenericEntity<Map<String, FavoriteMovieXmlRootElement>>> entity = Entity.entity(genericEntity, MediaType.APPLICATION_JSON_TYPE);

        Response response = target.request().post(entity);

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String result = response.readEntity(String.class);
        log.info("Result: " + result);
        Assert.assertTrue(!result.contains("xx:xx:xx:xx:xx:xx:xx"));
    }

    private static Archive<?> deploy(String deploymentName) {
        WebArchive war = TestUtil.prepareArchive(deploymentName);
        war.addClasses(FavoriteMovie.class, FavoriteMovieXmlType.class,
                FavoriteMovieXmlRootElement.class, ObjectFactory.class);
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");

        Map<String, String> contextParams = new Hashtable<>();
        if (!deploymentName.equals(DEFAULT)) {
            contextParams.put(ResteasyContextParameters.RESTEASY_EXPAND_ENTITY_REFERENCES, deploymentName);
        }

        return TestUtil.finishContainerPrepare(war, contextParams, JettisonMovieResource.class);
    }
}
