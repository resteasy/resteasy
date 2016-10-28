package org.jboss.resteasy.test.cdi.inheritence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceBook;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceBookSpecialized;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceInheritanceResource;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceSelectBook;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceStereotypeAlternative;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails This class tests CDI inheritance (BookSpecialized extends Book)
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SpecializedInheritanceTest {
    protected static final Logger log = LogManager.getLogger(SpecializedInheritanceTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(SpecializedInheritanceTest.class.getSimpleName());
        war.addClasses(UtilityProducer.class)
                .addClasses(CDIInheritenceSelectBook.class, CDIInheritenceStereotypeAlternative.class)
                .addClasses(CDIInheritenceBook.class, CDIInheritenceBookSpecialized.class, CDIInheritenceInheritanceResource.class)
                .addAsWebInfResource(SpecializedInheritanceTest.class.getPackage(), "specializedBeans.xml", "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Client get request. Resource check inheritance bean on server.
     * @tpPassCrit Response status should not contain error.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAlternative() throws Exception {
        Client client = ClientBuilder.newClient();
        log.info("starting testAlternative()");
        WebTarget base = client.target(PortProviderUtil.generateURL("/specialized/", SpecializedInheritanceTest.class.getSimpleName()));
        Response response = base.request().get();
        log.info("Response status: " + response.getStatus());
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
        client.close();
    }
}
