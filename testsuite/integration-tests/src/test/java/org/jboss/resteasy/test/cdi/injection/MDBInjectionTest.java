package org.jboss.resteasy.test.cdi.injection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardInteroperability;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBook;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookBag;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookBagLocal;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookCollection;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookMDB;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookReader;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookResource;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionBookWriter;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionDependentScoped;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionNewBean;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionResourceProducer;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionScopeInheritingStereotype;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionScopeStereotype;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStatefulEJB;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStereotypedApplicationScope;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStereotypedDependentScope;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionUnscopedResource;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Counter;
import org.jboss.resteasy.test.cdi.util.PersistenceUnitProducer;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails This class tests the use of MDBs with Resteasy, including the injection of a
 *                    JAX-RS resource into an MDB.
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({ NotForForwardInteroperability.class }) // specific queue settings works only on EAP7
public class MDBInjectionTest extends AbstractInjectionTestBase {
    protected static final Logger log = LogManager.getLogger(MDBInjectionTest.class.getName());

    static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(MDBInjectionTest.class.getSimpleName());
        war.addClass(AbstractInjectionTestBase.class)
                .addClasses(CDIInjectionBook.class, CDIInjectionBookResource.class, Constants.class, UtilityProducer.class)
                .addClasses(Counter.class, CDIInjectionBookCollection.class, CDIInjectionBookReader.class, CDIInjectionBookWriter.class)
                .addClasses(CDIInjectionDependentScoped.class, CDIInjectionStatefulEJB.class, CDIInjectionUnscopedResource.class)
                .addClasses(CDIInjectionBookBagLocal.class, CDIInjectionBookBag.class)
                .addClasses(CDIInjectionBookMDB.class)
                .addClasses(CDIInjectionNewBean.class)
                .addClasses(CDIInjectionScopeStereotype.class, CDIInjectionScopeInheritingStereotype.class)
                .addClasses(CDIInjectionStereotypedApplicationScope.class, CDIInjectionStereotypedDependentScope.class)
                .addClasses(Resource.class, CDIInjectionResourceProducer.class, PersistenceUnitProducer.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource(InjectionTest.class.getPackage(), "persistence.xml", "META-INF/persistence.xml");
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Before
    public void preparePersistenceTest() throws Exception {
        log.info("Dumping old records.");
        WebTarget base = client.target(PortProviderUtil.generateURL("/empty/", MDBInjectionTest.class.getSimpleName()));
        Response response = base.request().post(Entity.text(new String()));
        response.close();
    }

    /**
     * @tpTestDetails Tests the injection of JMS Producers, Consumers, Queues, and MDBs using producer fields and methods.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testMDB() throws Exception {
        log.info("starting testJMS()");

        // Send a book title.
        WebTarget base = client.target(PortProviderUtil.generateURL("/produceMessage/", MDBInjectionTest.class.getSimpleName()));
        String title = "Dead Man Lounging";
        CDIInjectionBook book = new CDIInjectionBook(23, title);
        Response response = base.request().post(Entity.entity(book, Constants.MEDIA_TYPE_TEST_XML));
        log.info("status: " + response.getStatus());
        log.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        // Verify that the received book title is the one that was sent.
        base = client.target(PortProviderUtil.generateURL("/mdb/consumeMessage/", MDBInjectionTest.class.getSimpleName()));
        response = base.request().get();
        log.info("status: " + response.getStatus());
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response", title, response.readEntity(String.class));
        response.close();
    }
}
