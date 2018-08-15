package org.jboss.resteasy.test.providers.jaxb;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJavaTypeAdapterResource;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJavaTypeAdapterAlien;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJavaTypeAdapterAlienAdapter;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJavaTypeAdapterHuman;
import org.jboss.resteasy.test.providers.jaxb.resource.XmlJavaTypeAdapterFoo;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class XmlJavaTypeAdapterTest {

    private final Logger logger = Logger.getLogger(XmlJavaTypeAdapterTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XmlJavaTypeAdapterTest.class.getSimpleName());
        war.addClass(XmlJavaTypeAdapterTest.class);
        // Arquillian in the deployment and use of PortProviderUtil in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new PropertyPermission("node", "read"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, XmlJavaTypeAdapterAlien.class, XmlJavaTypeAdapterAlienAdapter.class,
                XmlJavaTypeAdapterFoo.class, XmlJavaTypeAdapterHuman.class, XmlJavaTypeAdapterResource.class, PortProviderUtil.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, XmlJavaTypeAdapterTest.class.getSimpleName());
    }

    public static class Tralfamadorean extends XmlJavaTypeAdapterAlien {
    }

    /**
     * @tpTestDetails Tests jaxb resource is returning correct string with @XmlJavaTypeAdapter in place
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @RunAsClient
    public void testPostHuman() {
        ResteasyWebTarget target = client.target(generateURL("/human"));
        XmlJavaTypeAdapterHuman human = new XmlJavaTypeAdapterHuman();
        human.setName("bill");
        String response = target.request().post(Entity.entity(human, MediaType.APPLICATION_XML_TYPE), String.class);
        Assert.assertEquals("The received response was not the expected one", "bill", response);
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning Foo object
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @RunAsClient
    public void testPostFooToFoo() {
        ResteasyWebTarget target = client.target(generateURL("/foo/foo"));
        XmlJavaTypeAdapterFoo foo = new XmlJavaTypeAdapterFoo();
        foo.setName("bill");
        XmlJavaTypeAdapterFoo response = target.request().post(Entity.entity(foo, MediaType.APPLICATION_XML_TYPE), XmlJavaTypeAdapterFoo.class);
        Assert.assertEquals("The received response was not the expected one", foo, response);
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning String
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @RunAsClient
    public void testPostFooToString() {
        ResteasyWebTarget target = client.target(generateURL("/foo/foo"));
        XmlJavaTypeAdapterFoo foo = new XmlJavaTypeAdapterFoo();
        foo.setName("bill");
        String response = target.request().post(Entity.entity(foo, MediaType.APPLICATION_XML_TYPE), String.class);
        logger.info("response: \"" + response + "\"");
        Assert.assertTrue("The received response was not the expected one",
                response.contains("<xmlJavaTypeAdapterFoo><alien><name>llib</name></alien></xmlJavaTypeAdapterFoo>"));
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning list of Human objects
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @RunAsClient
    public void testPostHumanList() {
        ResteasyWebTarget target = client.target(generateURL("/list/human"));
        List<XmlJavaTypeAdapterHuman> list = new ArrayList<XmlJavaTypeAdapterHuman>();
        XmlJavaTypeAdapterHuman human = new XmlJavaTypeAdapterHuman();
        human.setName("bill");
        list.add(human);
        human = new XmlJavaTypeAdapterHuman();
        human.setName("bob");
        list.add(human);
        GenericEntity<List<XmlJavaTypeAdapterHuman>> entity = new GenericEntity<List<XmlJavaTypeAdapterHuman>>(list) {
        };
        String response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), String.class);
        Assert.assertEquals("The received response was not the expected one", "|bill|bob", response);
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning list of Alien objects,
     * where application expects the use of Human class with jaxb annotation, XmlJavaTypeAdapter is used to convert Alien
     * to Human and back
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostAlienList() {
        ResteasyWebTarget target = client.target(generateURL("/list/alien"));
        List<XmlJavaTypeAdapterAlien> list = new ArrayList<XmlJavaTypeAdapterAlien>();
        XmlJavaTypeAdapterAlien alien1 = new XmlJavaTypeAdapterAlien();
        alien1.setName("bill");
        list.add(alien1);
        XmlJavaTypeAdapterAlien alien2 = new XmlJavaTypeAdapterAlien();
        alien2.setName("bob");
        list.add(alien2);
        GenericEntity<List<XmlJavaTypeAdapterAlien>> entity = new GenericEntity<List<XmlJavaTypeAdapterAlien>>(list) {
        };
        GenericType<List<XmlJavaTypeAdapterAlien>> alienListType = new GenericType<List<XmlJavaTypeAdapterAlien>>() {
        };
        List<XmlJavaTypeAdapterAlien> response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienListType);
        logger.info("response: \"" + response + "\"");
        Assert.assertEquals("The received response was not the expected one", 2, response.size());
        Assert.assertTrue("The received response was not the expected one", response.contains(alien1));
        Assert.assertTrue("The received response was not the expected one", response.contains(alien2));
        Assert.assertEquals("The marshalling of the Alien didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.marshalCounter.get());
        Assert.assertEquals("The unmarshalling of the Human didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.get());
        XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.set(0);
        XmlJavaTypeAdapterAlienAdapter.marshalCounter.set(0);
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning array of Alien objects,
     * where application expects the use of Human class with jaxb annotation, XmlJavaTypeAdapter is used to convert Alien
     * to Human and back
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostAlienArray() {
        ResteasyWebTarget target = client.target(generateURL("/array/alien"));
        XmlJavaTypeAdapterAlien[] array = new XmlJavaTypeAdapterAlien[2];
        XmlJavaTypeAdapterAlien alien1 = new XmlJavaTypeAdapterAlien();
        alien1.setName("bill");
        array[0] = alien1;
        XmlJavaTypeAdapterAlien alien2 = new XmlJavaTypeAdapterAlien();
        alien2.setName("bob");
        array[1] = alien2;
        GenericEntity<XmlJavaTypeAdapterAlien[]> entity = new GenericEntity<XmlJavaTypeAdapterAlien[]>(array) {
        };
        GenericType<XmlJavaTypeAdapterAlien[]> alienArrayType = new GenericType<XmlJavaTypeAdapterAlien[]>() {
        };
        XmlJavaTypeAdapterAlien[] response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienArrayType);
        logger.info("response: \"" + response + "\"");
        Assert.assertEquals("The received response was not the expected one", 2, response.length);
        Assert.assertTrue("The received response was not the expected one",
                (alien1.equals(response[0]) && alien2.equals(response[1])) || (alien1.equals(response[1]) && alien2.equals(response[0])));
        Assert.assertEquals("The marshalling of the Alien didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.marshalCounter.get());
        Assert.assertEquals("The unmarshalling of the Human didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.get());
        XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.set(0);
        XmlJavaTypeAdapterAlienAdapter.marshalCounter.set(0);
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning map of Alien objects,
     * where application expects the use of Human class with jaxb annotation, XmlJavaTypeAdapter is used to convert Alien
     * to Human and back
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostAlienMap() {
        ResteasyWebTarget target = client.target(generateURL("/map/alien"));
        Map<String, XmlJavaTypeAdapterAlien> map = new HashMap<String, XmlJavaTypeAdapterAlien>();
        XmlJavaTypeAdapterAlien alien1 = new XmlJavaTypeAdapterAlien();
        alien1.setName("bill");
        map.put("abc", alien1);
        XmlJavaTypeAdapterAlien alien2 = new XmlJavaTypeAdapterAlien();
        alien2.setName("bob");
        map.put("xyz", alien2);
        GenericEntity<Map<String, XmlJavaTypeAdapterAlien>> entity = new GenericEntity<Map<String, XmlJavaTypeAdapterAlien>>(map) {
        };
        GenericType<Map<String, XmlJavaTypeAdapterAlien>> alienMapType = new GenericType<Map<String, XmlJavaTypeAdapterAlien>>() {
        };
        Map<String, XmlJavaTypeAdapterAlien> response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienMapType);
        logger.info("response: \"" + response + "\"");
        Assert.assertEquals("The received response was not the expected one", 2, response.size());
        Assert.assertTrue("The received response was not the expected one", alien1.equals(response.get("abc")));
        Assert.assertTrue("The received response was not the expected one", alien2.equals(response.get("xyz")));
        Assert.assertEquals("The marshalling of the Alien didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.marshalCounter.get());
        Assert.assertEquals("The unmarshalling of the Human didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.get());
        XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.set(0);
        XmlJavaTypeAdapterAlienAdapter.marshalCounter.set(0);
    }

    /**
     * @tpTestDetails Tests jaxb with class annotated by @XmlJavaTypeAdapter, resource returning list of Alien objects,
     * where application expects the use of Human class with jaxb annotation, XmlJavaTypeAdapter is used to convert Alien
     * to Human and back. The Entity send to the server extends Alien class.
     * @tpInfo RESTEASY-1088
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostTralfamadoreanList() {
        ResteasyWebTarget target = client.target(generateURL("/list/alien"));
        List<XmlJavaTypeAdapterAlien> list = new ArrayList<XmlJavaTypeAdapterAlien>();
        Tralfamadorean tralfamadorean1 = new Tralfamadorean();
        tralfamadorean1.setName("bill");
        list.add(tralfamadorean1);
        Tralfamadorean tralfamadorean2 = new Tralfamadorean();
        tralfamadorean2.setName("bob");
        list.add(tralfamadorean2);
        GenericEntity<List<XmlJavaTypeAdapterAlien>> entity = new GenericEntity<List<XmlJavaTypeAdapterAlien>>(list) {
        };
        GenericType<List<XmlJavaTypeAdapterAlien>> alienListType = new GenericType<List<XmlJavaTypeAdapterAlien>>() {
        };
        List<XmlJavaTypeAdapterAlien> response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienListType);
        logger.info("response: \"" + response + "\"");
        Assert.assertEquals("The received response was not the expected one", 2, response.size());
        Assert.assertTrue("The received response was not the expected one", response.contains(tralfamadorean1));
        Assert.assertTrue("The received response was not the expected one", response.contains(tralfamadorean2));
        Assert.assertEquals("The marshalling of the Alien didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.marshalCounter.get());
        Assert.assertEquals("The unmarshalling of the Human didn't happen the correct way",
                4, XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.get());
        XmlJavaTypeAdapterAlienAdapter.unmarshalCounter.set(0);
        XmlJavaTypeAdapterAlienAdapter.marshalCounter.set(0);
    }
}
