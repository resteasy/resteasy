package org.jboss.resteasy.test.cdi.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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
@Tag("NotForBootableJar")
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
        WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-reverse-injection-test.war");
        war.addClass(TestApplication.class)
                .addClasses(
                        CDIInjectionBook.class,
                        CDIInjectionBookBag.class,
                        CDIInjectionBookBagLocal.class,
                        CDIInjectionBookCollection.class,
                        CDIInjectionBookMDB.class,
                        CDIInjectionBookReader.class,
                        CDIInjectionBookResource.class,
                        CDIInjectionBookWriter.class,
                        CDIInjectionDependentScoped.class,
                        CDIInjectionNewBean.class,
                        CDIInjectionResourceBinding.class,
                        CDIInjectionResourceProducer.class,
                        CDIInjectionStatefulEJB.class,
                        CDIInjectionStereotypedApplicationScope.class,
                        CDIInjectionStereotypedDependentScope.class,
                        CDIInjectionUnscopedResource.class,
                        Constants.class,
                        Counter.class,
                        PersistenceUnitProducer.class,
                        PortProviderUtil.class,
                        ReverseInjectionEJBHolder.class,
                        ReverseInjectionEJBHolderLocal.class,
                        ReverseInjectionEJBHolderRemote.class,
                        ReverseInjectionEJBInterface.class,
                        ReverseInjectionResource.class,
                        ReverseInjectionTest.class,
                        StatefulApplicationScopedEJBwithJaxRsComponents.class,
                        StatefulApplicationScopedEJBwithJaxRsComponentsInterface.class,
                        StatefulDependentScopedEJBwithJaxRsComponents.class,
                        StatefulDependentScopedEJBwithJaxRsComponentsInterface.class,
                        StatefulRequestScopedEJBwithJaxRsComponents.class,
                        StatefulRequestScopedEJBwithJaxRsComponentsInterface.class,
                        StatelessEJBwithJaxRsComponents.class,
                        StatelessEJBwithJaxRsComponentsInterface.class,
                        Utilities.class,
                        UtilityProducer.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml")
                .addAsResource(ReverseInjectionTest.class.getPackage(), "persistence.xml", "META-INF/persistence.xml");
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
    public void testMDB() throws NamingException {
        String destinationName = "jms/queue/test";
        Context ic = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
        try (Connection connection = cf.createConnection()) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer((Queue) ic.lookup(destinationName));
            connection.start();

            CDIInjectionBook book1 = createNewBook("Dead Man Snoring", session, producer);
            CDIInjectionBook book2 = createNewBook("Dead Man Drooling", session, producer);

            waitAndAssert(URI.create(generateURL("/mdb/books")), books -> {
                assertEquals(2, books.size(), "Wrong number of books received");
                var names = books.stream().map(CDIInjectionBook::getName).toList();
                assertTrue(names.contains(book1.getName()) && names.contains(book2.getName()),
                        "Expected books not found");
            });
        } catch (Exception exc) {
            logStacktrace(exc);
            Assertions.fail(exc);
        }
    }

    private void logStacktrace(Exception exc) {
        StringWriter errors = new StringWriter();
        exc.printStackTrace(new PrintWriter(errors));
        log.error(errors.toString());
    }

    private CDIInjectionBook createNewBook(String title, Session session, MessageProducer producer) throws JMSException {
        var book = new CDIInjectionBook(title);
        TextMessage message = session.createTextMessage(book.getName());
        producer.send(message);
        log.info("Message sent to the JMS Provider: " + book.getName());
        return book;
    }

    public void waitAndAssert(URI uri, Consumer<List<CDIInjectionBook>> assertionConsumer) throws AssertionError {
        log.info("waitAndAssert(..) validation starting.");
        Instant endTime = Instant.now().plus(Duration.of(30, ChronoUnit.SECONDS));
        AssertionError lastAssertionError = null;

        while (Instant.now().isBefore(endTime)) {
            try (Response response = client.target(uri).request().get()) {
                assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                assertionConsumer.accept(response.readEntity(new GenericType<>(BookCollectionType)));
                return;
            } catch (AssertionError assertionError) {
                log.debug("waitAndAssert(..) validation failed - retrying.");
                lastAssertionError = assertionError;
                Thread.onSpinWait();
            }
        }

        throw Objects.requireNonNullElseGet(lastAssertionError, AssertionError::new);
    }
}
