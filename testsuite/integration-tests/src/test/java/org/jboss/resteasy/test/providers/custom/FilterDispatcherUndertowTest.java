package org.jboss.resteasy.test.providers.custom;

import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.custom.resource.FilterDispatcherForwardServlet;
import org.jboss.resteasy.test.providers.custom.resource.FilterDispatcherServlet;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-903
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FilterDispatcherUndertowTest {
    private static final Logger logger = LogManager.getLogger(FilterDispatcherUndertowTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FilterDispatcherUndertowTest.class.getSimpleName());
        war.addClass(FilterDispatcherForwardServlet.class);
        war.addClass(FilterDispatcherServlet.class);
        war.addAsWebInfResource(FilterDispatcherUndertowTest.class.getPackage(), "FilterDispatcherManifestWeb.xml", "web.xml");
        war.addAsWebInfResource(FilterDispatcherUndertowTest.class.getPackage(), "FilterDispatcherManifest.MF", "MANIFEST.MF");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Server should be able to forward a HttpServletRequest/HttpServletResponse captured
     *                using the @Context annotation on a member variables inside a resource class.
     * @tpPassCrit Response should have code 200.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUndertow() throws Exception {
        logger.info("starting testUndertow()");
        URL url = new URL(PortProviderUtil.generateURL("/test", FilterDispatcherUndertowTest.class.getSimpleName()));
        HttpURLConnection conn = HttpURLConnection.class.cast(url.openConnection());
        conn.connect();
        logger.info("Connection status: " + conn.getResponseCode());
        byte[] b = new byte[16];
        conn.getInputStream().read(b);
        logger.info("Response result: " + new String(b));
        Assert.assertEquals(HttpResponseCodes.SC_OK, conn.getResponseCode());
        conn.disconnect();
    }
}

