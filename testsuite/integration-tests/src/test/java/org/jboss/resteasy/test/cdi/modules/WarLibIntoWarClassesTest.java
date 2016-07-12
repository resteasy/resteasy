package org.jboss.resteasy.test.cdi.modules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesInjectable;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesInjectableBinder;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesInjectableIntf;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesModulesResource;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesModulesResourceIntf;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
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
 * @tpTestCaseDetails Test bean injection in lib of war.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WarLibIntoWarClassesTest {
    protected static final Logger log = LogManager.getLogger(WarLibIntoWarClassesTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(CDIModulesInjectableBinder.class, CDIModulesInjectableIntf.class, CDIModulesInjectable.class)
                .add(EmptyAsset.INSTANCE, "META-INF/beans.xml");
        WebArchive war = TestUtil.prepareArchive(WarLibIntoWarClassesTest.class.getSimpleName())
                .addClasses(UtilityProducer.class)
                .addClasses(CDIModulesModulesResourceIntf.class, CDIModulesModulesResource.class)
                .addAsLibrary(jar)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    /**
     * @tpTestDetails Test bean injection in lib of war.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testModules() throws Exception {
        log.info("starting testModules()");
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(PortProviderUtil.generateURL("/modules/test/", WarLibIntoWarClassesTest.class.getSimpleName()));
        Response response = base.request().get();
        log.info("Status: " + response.getStatus());
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
        client.close();
    }
}
