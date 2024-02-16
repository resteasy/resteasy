package org.jboss.resteasy.test.cdi.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;
import java.net.SocketPermission;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import javax.naming.Context;
import javax.naming.InitialContext;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
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
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionResourceBinding;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionResourceProducer;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStatefulEJB;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStereotypedApplicationScope;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionStereotypedDependentScope;
import org.jboss.resteasy.test.cdi.injection.resource.CDIInjectionUnscopedResource;
import org.jboss.resteasy.test.cdi.injection.resource.ReverseInjectionEJBHolder;
import org.jboss.resteasy.test.cdi.injection.resource.ReverseInjectionEJBHolderLocal;
import org.jboss.resteasy.test.cdi.injection.resource.ReverseInjectionEJBHolderRemote;
import org.jboss.resteasy.test.cdi.injection.resource.ReverseInjectionEJBInterface;
import org.jboss.resteasy.test.cdi.injection.resource.ReverseInjectionResource;
import org.jboss.resteasy.test.cdi.injection.resource.StatefulApplicationScopedEJBwithJaxRsComponents;
import org.jboss.resteasy.test.cdi.injection.resource.StatefulApplicationScopedEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.test.cdi.injection.resource.StatefulDependentScopedEJBwithJaxRsComponents;
import org.jboss.resteasy.test.cdi.injection.resource.StatefulDependentScopedEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.test.cdi.injection.resource.StatefulRequestScopedEJBwithJaxRsComponents;
import org.jboss.resteasy.test.cdi.injection.resource.StatefulRequestScopedEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.test.cdi.injection.resource.StatelessEJBwithJaxRsComponents;
import org.jboss.resteasy.test.cdi.injection.resource.StatelessEJBwithJaxRsComponentsInterface;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Counter;
import org.jboss.resteasy.test.cdi.util.PersistenceUnitProducer;
import org.jboss.resteasy.test.cdi.util.Utilities;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ReverseInjectionTest goes beyond InjectionTest by injecting Resteasy objects
 *                    into other kinds of beans, e.g., EJBs. For example,
 *                    *) an EJB called ReverseInjectionEJBHolder is injected into the Resteasy resource ReverseInjectionResource
 *                    *) a variety of EJBs, e.g., StatelessEJBwithJaxRsComponents, are injected into ReverseInjectionEJBHolder
 *                    *) a variety of Resteasy resources are injected into StatelessEJBwithJaxRsComponents and similar EJBs.
 *                    Also, the EJBs like StatelessEJBwithJaxRsComponents are injected into ReverseInjectionEJBHolder using
 *                    both.
 *                    Annotation @EJB and @Inject, and the semantics of both are tested.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@ServerSetup(JmsTestQueueSetupTask.class)
public class ReverseInjectionTest {
    private static Logger log = Logger.getLogger(ReverseInjectionTest.class);

    Client client;

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
    }

    static ParameterizedType BookCollectionType = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] { CDIInjectionBook.class };
        }

        @Override
        public Type getRawType() {
            return Collection.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    @Deployment
    public static Archive<?> createTestArchive() throws Exception {
        WebArchive war = TestUtil.prepareArchive("resteasy-reverse-injection-test")
                .addClasses(ReverseInjectionTest.class, PortProviderUtil.class)
                .addClasses(Constants.class, PersistenceUnitProducer.class, UtilityProducer.class, Utilities.class)
                .addClasses(CDIInjectionBook.class, CDIInjectionBookResource.class)
                .addClasses(CDIInjectionResourceBinding.class, CDIInjectionResourceProducer.class)
                .addClasses(Counter.class, CDIInjectionBookCollection.class, CDIInjectionBookReader.class,
                        CDIInjectionBookWriter.class)
                .addClasses(CDIInjectionDependentScoped.class, CDIInjectionStatefulEJB.class,
                        CDIInjectionUnscopedResource.class)
                .addClasses(CDIInjectionBookBagLocal.class, CDIInjectionBookBag.class)
                .addClasses(CDIInjectionBookMDB.class)
                .addClasses(ReverseInjectionEJBInterface.class)
                .addClasses(StatelessEJBwithJaxRsComponentsInterface.class, StatelessEJBwithJaxRsComponents.class)
                .addClasses(StatefulDependentScopedEJBwithJaxRsComponentsInterface.class,
                        StatefulDependentScopedEJBwithJaxRsComponents.class)
                .addClasses(StatefulRequestScopedEJBwithJaxRsComponentsInterface.class,
                        StatefulRequestScopedEJBwithJaxRsComponents.class)
                .addClasses(StatefulApplicationScopedEJBwithJaxRsComponentsInterface.class,
                        StatefulApplicationScopedEJBwithJaxRsComponents.class)
                .addClasses(ReverseInjectionEJBHolderRemote.class, ReverseInjectionEJBHolderLocal.class,
                        ReverseInjectionEJBHolder.class)
                .addClasses(ReverseInjectionResource.class)
                .addClasses(CDIInjectionNewBean.class, CDIInjectionStereotypedApplicationScope.class,
                        CDIInjectionStereotypedDependentScope.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml")
                .addAsResource(ReverseInjectionTest.class.getPackage(), "persistence.xml", "META-INF/persistence.xml");
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, "resteasy-reverse-injection-test");
    }

    /**
     * @tpTestDetails Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
     *                a stateless EJB3. The target SLSB is not a contextual object, since it is
     *                obtained through JNDI, so CDI performs injections when the SLSB is created,
     *                but there is no scope management. It follows that the target SLSB is not recreated
     *                for the second invocation.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSLSB() throws Exception {
        log.info("starting testSLSB()");

        final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        String name = "java:global/resteasy-reverse-injection-test/StatelessEJBwithJaxRsComponents!"
                + StatelessEJBwithJaxRsComponentsInterface.class.getName();
        StatelessEJBwithJaxRsComponentsInterface remote = StatelessEJBwithJaxRsComponentsInterface.class
                .cast(context.lookup(name));
        assertNotNull(remote, "Bean should not be null");
        log.info("remote: " + remote.toString());
        remote.setUp(ReverseInjectionResource.NON_CONTEXTUAL);
        assertTrue(remote.test(ReverseInjectionResource.NON_CONTEXTUAL), "Call bean method faild");
    }

    /**
     * @tpTestDetails Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
     *                a @Dependent annotated stateful EJB3. The target SFSB is not a contextual object,
     *                since it is obtained through JNDI, so CDI performs injections when the SFSB is created,
     *                but there is no scope management. It follows that the target SFSB is not recreated
     *                for the second invocation.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSFSBDependentScope() throws Exception {
        log.info("starting testSFSBDependentScope()");
        doTestSFSB("Dependent");
    }

    /**
     * @tpTestDetails Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
     *                a @RequestScoped annotated stateful EJB3. The target SFSB is not a contextual object,
     *                since it is obtained through JNDI, so CDI performs injections when the SFSB is created,
     *                but there is no scope management. It follows that the target SFSB is not recreated
     *                for the second invocation.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSFSBRequestScope() throws Exception {
        log.info("starting testSFSBRequestScope()");
        doTestSFSB("Request");

    }

    /**
     * @tpTestDetails Addresses injection of JAX-RS components (BookResource, BookReader, BookWriter) into
     *                a @ApplicationScoped annotated stateful EJB3. The target SFSB is not a contextual object,
     *                since it is obtained through JNDI, so CDI performs injections when the SFSB is created,
     *                but there is no scope management. It follows that the target SFSB is not recreated
     *                for the second invocation.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSFSBApplicationScope() throws Exception {
        log.info("starting testSFSBApplicationScope()");
        doTestSFSB("Application");

    }

    private void doTestSFSB(String scope) throws Exception {
        final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        String className = "Stateful" + scope + "ScopedEJBwithJaxRsComponents";
        Class<?> viewName = Class.forName("org.jboss.resteasy.test.cdi.injection.resource." + className + "Interface");
        String lookup = "java:global/resteasy-reverse-injection-test/" + className + "!" + viewName.getName();
        log.info("lookup: " + lookup);
        ReverseInjectionEJBInterface remote = ReverseInjectionEJBInterface.class.cast(context.lookup(lookup));
        log.info("remote: " + remote);
        remote.setUp(ReverseInjectionResource.NON_CONTEXTUAL);
        assertTrue(remote.test(ReverseInjectionResource.NON_CONTEXTUAL), "Call bean method faild");
    }

    /**
     * @tpTestDetails Verifies the scopes of the EJBs used in this set of tests.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEJBHolderInResourceScopes() throws Exception {
        WebTarget base = client.target(generateURL("/reverse/testScopes/"));
        Response response = base.request().post(Entity.text(new String()));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test static hash map in RequestScoped bean used as REST point.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEJBHolderInResource() throws Exception {
        WebTarget base = client.target(generateURL("/reverse/setup/"));
        Response response = base.request().post(Entity.text(new String()));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/reverse/test/"));
        response = base.request().post(Entity.text(new String()));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Tests the injection of a JAX-RS resource (BookResource) into an MDB.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @Disabled("RESTEASY-2962")
    public void testMDB() throws Exception {
        String destinationName = "queue/test";
        Context ic;
        ConnectionFactory cf;
        Connection connection = null;
        try {
            ic = new InitialContext();
            cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
            Queue queue = (Queue) ic.lookup(destinationName);
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            connection.start();
            CDIInjectionBook book1 = new CDIInjectionBook("Dead Man Snoring");
            TextMessage message = session.createTextMessage(book1.getName());
            producer.send(message);
            log.info("Message sent to the JMS Provider: " + book1.getName());
            CDIInjectionBook book2 = new CDIInjectionBook("Dead Man Drooling");
            message = session.createTextMessage(book2.getName());
            producer.send(message);
            log.info("Message sent to the JMS Provider: " + book2.getName());
            WebTarget base = client.target(generateURL("/mdb/books"));
            Response response = base.request().get();
            log.info("status: " + response.getStatus());
            assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            @SuppressWarnings("unchecked")
            Collection<CDIInjectionBook> books = response.readEntity(new GenericType<>(BookCollectionType));
            log.info("Collection: " + books);
            assertEquals(2, books.size(), "Wrong count of received items");
            Iterator<CDIInjectionBook> it = books.iterator();
            CDIInjectionBook b1 = it.next();
            CDIInjectionBook b2 = it.next();
            assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1),
                    "Book is not inject correctly");
        } catch (Exception exc) {
            StringWriter errors = new StringWriter();
            exc.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    log.error(errors.toString());
                }
            }
        }
    }
}
