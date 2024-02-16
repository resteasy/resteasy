package org.jboss.resteasy.test.cdi.extensions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBoston;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonBean;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonBeanExtension;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonHolder;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBostonlLeaf;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsResource;
import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsTestReader;
import org.jboss.resteasy.test.cdi.util.Utilities;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test CDI extensions for bean.
 *                    BostonBeanExtension implements a CDI extension, it creates a BostonBean for each of the two classes,
 *                    BostonHolder and BostonLeaf, that are annotated with @Boston, and it registers them with the CDI runtime.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BeanExtensionTest {
    protected static final Logger log = Logger.getLogger(BeanExtensionTest.class.getName());

    @SuppressWarnings(value = "unchecked")
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(BeanExtensionTest.class.getSimpleName());
        war.addClasses(UtilityProducer.class, Utilities.class)
                .addClasses(CDIExtensionsBostonBeanExtension.class, CDIExtensionsBoston.class, CDIExtensionsBostonBean.class)
                .addClasses(CDIExtensionsResource.class, CDIExtensionsTestReader.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml")
                .addAsServiceProvider(Extension.class, CDIExtensionsBostonBeanExtension.class);

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class).addClasses(CDIExtensionsBostonHolder.class,
                CDIExtensionsBostonlLeaf.class);
        war.addAsLibrary(jar);

        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Client get request. Resource check extension bean on server.
     * @tpPassCrit Response status should not contain error.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBostonBeans() throws Exception {
        log.info("starting testBostonBeans()");

        Client client = ClientBuilder.newClient();
        WebTarget base = client
                .target(PortProviderUtil.generateURL("/extension/boston/", BeanExtensionTest.class.getSimpleName()));
        Response response = base.request().post(Entity.text(new String()));

        log.info("Response status: " + response.getStatus());

        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        response.close();
        client.close();
    }
}
