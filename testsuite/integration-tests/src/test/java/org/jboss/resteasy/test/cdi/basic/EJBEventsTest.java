package org.jboss.resteasy.test.cdi.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.basic.resource.EJBBook;
import org.jboss.resteasy.test.cdi.basic.resource.EJBEventsObserver;
import org.jboss.resteasy.test.cdi.basic.resource.EJBEventsObserverImpl;
import org.jboss.resteasy.test.cdi.basic.resource.EJBEventsSource;
import org.jboss.resteasy.test.cdi.basic.resource.EJBEventsSourceImpl;
import org.jboss.resteasy.test.cdi.basic.resource.EJBEventsProcessRead;
import org.jboss.resteasy.test.cdi.basic.resource.EJBEventsProcessReadWrite;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails EJB, Events and RESTEasy integration test.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class EJBEventsTest {
    @Inject
    private Logger log;

    @Inject
    EJBEventsSource eventSource;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-ejb-test.war")
                .addClasses(UtilityProducer.class, EJBBook.class)
                .addClasses(EJBEventsObserver.class, EJBEventsObserverImpl.class)
                .addClasses(EJBEventsSource.class, EJBEventsSourceImpl.class)
                .addClasses(EJBEventsProcessRead.class, EJBEventsProcessReadWrite.class)
                .setWebXML(EJBEventsTest.class.getPackage(), "ejbtest_web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new PropertyPermission("arquillian.*", "read")), "permissions.xml");
        return war;
    }

    /**
     * @tpTestDetails Invokes additional methods of JAX-RS resource as local EJB.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsLocalEJB() throws Exception {
        log.info("entering testAsLocalEJB()");

        // Create book.
        EJBBook book1 = new EJBBook("RESTEasy: the Sequel");
        int id1 = eventSource.createBook(book1);
        log.info("id1: " + id1);
        assertEquals("Wrong ID of created book", 0, id1);

        // Create another book.
        EJBBook book2 = new EJBBook("RESTEasy: It's Alive");
        int id2 = eventSource.createBook(book2);
        log.info("id2: " + id2);
        assertEquals("Wrong ID of created book", 1, id2);

        // Retrieve first book.
        EJBBook bookResponse1 = eventSource.lookupBookById(id1);
        log.info("book1 response: " + bookResponse1);
        assertEquals("Wrong received book", book1, bookResponse1);

        // Retrieve second book.
        EJBBook bookResponse2 = eventSource.lookupBookById(id2);
        log.info("book2 response: " + bookResponse2);
        assertEquals("Wrong received book", book2, bookResponse2);
    }
}
