package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.resource.QualityFactorResource;
import org.jboss.resteasy.test.providers.jaxb.resource.QualityFactorThing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class QualityFactorTest {

    static ResteasyClient client;
    private static Logger logger = Logger.getLogger(QualityFactorTest.class.getName());


    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(QualityFactorTest.class.getSimpleName());
        war.addClass(JaxbCollectionTest.class);
        return TestUtil.finishContainerPrepare(war, null, QualityFactorResource.class, QualityFactorThing.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, QualityFactorTest.class.getSimpleName());
    }

    @Test
    public void testHeader() throws Exception {
        Response response = client.target(generateURL("/test")).request()
                .accept("application/xml; q=0.5", "application/json; q=0.8").get();
        String result = response.readEntity(String.class);
        logger.info(result);
        Assert.assertTrue("The format of the response doesn't reflect the quality factor", result.startsWith("{"));

    }
}
