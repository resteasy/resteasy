package org.jboss.resteasy.test.microprofile.restclient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionActivator;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionService;
import org.jboss.resteasy.test.microprofile.restclient.resource.MPCollectionServiceIntf;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.util.List;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Show how to get the proxy for the service.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MPCollectionTest {
    protected static final Logger LOG = Logger.getLogger(MPCollectionTest.class.getName());
    private static final String WAR_SERVICE = "MPCollectionTest";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(MPCollectionService.class,
                MPCollectionActivator.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WAR_SERVICE);
    }

    @Test
    public void testOne() throws Exception {
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        // uri is http://localhost:port/{context-root}
        MPCollectionServiceIntf mpc = builder.baseUri(URI.create(generateURL("")))
        .build(MPCollectionServiceIntf.class);
        List<String> l = mpc.getList();
        Assert.assertEquals(3, l.size());
    }
}
