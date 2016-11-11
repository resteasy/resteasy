package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.StringCharsetResource;
import org.jboss.resteasy.test.providers.jaxb.resource.StringCharsetRespond;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class StringCharsetTest {

    private final Logger logger = Logger.getLogger(ExceptionMapperJaxbTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(StringCharsetTest.class.getSimpleName());
        war.addClass(StreamResetTest.class);
        return TestUtil.finishContainerPrepare(war, null, StringCharsetResource.class, StringCharsetRespond.class);
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
        return PortProviderUtil.generateURL(path, StringCharsetTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests jaxb with combination of request specified charset
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/charset/test.xml"));
        String response = target.request().header("Accept", "application/xml;charset=iso-8859-2").get(String.class);
        logger.info(response);
        Assert.assertTrue("Response doesn't contain expected characters",
                response.contains("Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269));
    }
}
