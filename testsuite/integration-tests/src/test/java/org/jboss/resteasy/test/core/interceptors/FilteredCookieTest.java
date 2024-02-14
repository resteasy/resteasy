package org.jboss.resteasy.test.core.interceptors;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.interceptors.resource.FilteredCookieContainerRequestFilter;
import org.jboss.resteasy.test.core.interceptors.resource.FilteredCookieResource;
import org.jboss.resteasy.utils.CookieUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Cookies and filters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1266
 * @tpSince RESTEasy 3.1.0.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FilteredCookieTest {

    private static final String OLD_COOKIE_NAME = "old-cookie";
    private static final String NEW_COOKIE_NAME = "new-cookie";

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(FilteredCookieTest.class.getSimpleName())
                .addClass(CookieUtil.class);
        return TestUtil.finishContainerPrepare(war, null, FilteredCookieResource.class,
                FilteredCookieContainerRequestFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FilteredCookieTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests if multiple cookies are returned by the server
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testServerHeaders() {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test/get"));
        Response response = target.request().get();
        NewCookie cookie = response.getCookies().get(OLD_COOKIE_NAME);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(cookie);
        client.close();

        client = ClientBuilder.newClient();
        target = client.target(generateURL("/test/return"));
        Builder builder = target.request();
        response = builder.cookie(cookie).get();
        NewCookie oldCookie = response.getCookies().get(OLD_COOKIE_NAME);
        NewCookie newCookie = response.getCookies().get(NEW_COOKIE_NAME);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(oldCookie);
        Assertions.assertNotNull(newCookie);
        client.close();
    }
}
