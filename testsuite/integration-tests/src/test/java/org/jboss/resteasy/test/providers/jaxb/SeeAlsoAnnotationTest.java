package org.jboss.resteasy.test.providers.jaxb;

import java.io.StringWriter;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.xml.bind.JAXBContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.SeeAlsoAnnotationBaseFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.SeeAlsoAnnotationFooIntf;
import org.jboss.resteasy.test.providers.jaxb.resource.SeeAlsoAnnotationRealFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.SeeAlsoAnnotationResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SeeAlsoAnnotationTest {

    private final Logger logger = Logger.getLogger(SeeAlsoAnnotationTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SeeAlsoAnnotationTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SeeAlsoAnnotationResource.class, SeeAlsoAnnotationRealFoo.class,
                SeeAlsoAnnotationBaseFoo.class, SeeAlsoAnnotationFooIntf.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SeeAlsoAnnotationTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests jaxb @SeeAlsoAnnotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIntf() throws Exception {
        String url = generateURL("/see/intf");
        runTest(url);
    }

    /**
     * @tpTestDetails Tests jaxb @SeeAlsoAnnotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTest() throws Exception {
        String url = generateURL("/see/base");
        runTest(url);
    }

    private void runTest(String url) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(SeeAlsoAnnotationRealFoo.class);
        StringWriter writer = new StringWriter();
        SeeAlsoAnnotationRealFoo foo = new SeeAlsoAnnotationRealFoo();
        foo.setName("bill");

        ctx.createMarshaller().marshal(foo, writer);

        String s = writer.getBuffer().toString();
        logger.info(s);

        ResteasyWebTarget target = client.target(generateURL(url));
        target.request().header("Content-Type", "application/xml").put(Entity.xml(s));
    }

}
