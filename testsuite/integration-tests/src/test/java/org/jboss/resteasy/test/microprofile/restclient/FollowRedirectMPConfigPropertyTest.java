package org.jboss.resteasy.test.microprofile.restclient;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.microprofile.client.RestClientBuilderImpl;
import org.jboss.resteasy.test.microprofile.restclient.resource.FollowRedirectsService;
import org.jboss.resteasy.test.microprofile.restclient.resource.FollowRedirectsServiceIntf;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

/**
 * @tpSubChapter MicroProfile rest client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Show using microprofile-config property, "/mp-rest/followRedirects" works.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FollowRedirectMPConfigPropertyTest {
    protected static final Logger LOG = Logger.getLogger(FollowRedirectMPConfigPropertyTest.class.getName());
    private static final String WAR_SERVICE = "followRedirects_service";
    private static final String MP_REST_FOLLOWREDIRECT = "/mp-rest/followRedirects";

    @Deployment(name=WAR_SERVICE)
    public static Archive<?> serviceDeploy() {
        WebArchive war = TestUtil.prepareArchive(WAR_SERVICE);
        war.addClasses(FollowRedirectsService.class,
                PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    private static String generateURL(String path, String deployName) {
        return PortProviderUtil.generateURL(path, deployName);
    }

    @Test
    public void fullyQualifiedName() {
        String key = FollowRedirectsServiceIntf.class.getCanonicalName()+MP_REST_FOLLOWREDIRECT;
        System.setProperty(key, "true");

        RestClientBuilderImpl builder = new RestClientBuilderImpl();
        FollowRedirectsServiceIntf followRedirectsServiceIntf = builder
                .baseUri(URI.create(generateURL("", WAR_SERVICE)))
                .build(FollowRedirectsServiceIntf.class);

        Assert.assertTrue(builder.isFollowRedirects());
        System.clearProperty(key);
    }

    @Test
    public void simpleName() {
        String key = "ckName"+MP_REST_FOLLOWREDIRECT;
        System.setProperty(key, "true");

        RestClientBuilderImpl builder = new RestClientBuilderImpl();
        FollowRedirectsServiceIntf followRedirectsServiceIntf = builder
                .baseUri(URI.create(generateURL("", WAR_SERVICE)))
                .build(FollowRedirectsServiceIntf.class);

        Assert.assertTrue(builder.isFollowRedirects());
        System.clearProperty(key);
    }

    @Test
    public void badValue() {
        String key = "ckName"+MP_REST_FOLLOWREDIRECT;
        System.setProperty(key, "maybe");

        RestClientBuilderImpl builder = new RestClientBuilderImpl();
        FollowRedirectsServiceIntf followRedirectsServiceIntf = builder
                .baseUri(URI.create(generateURL("", WAR_SERVICE)))
                .build(FollowRedirectsServiceIntf.class);

        Assert.assertFalse(builder.isFollowRedirects());
        System.clearProperty(key);
    }
}
