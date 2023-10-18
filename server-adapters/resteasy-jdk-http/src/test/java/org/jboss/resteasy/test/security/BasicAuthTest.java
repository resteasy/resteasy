package org.jboss.resteasy.test.security;

import static org.jboss.resteasy.test.TestPortProvider.createProxy;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.jboss.resteasy.test.TestPortProvider.getHost;
import static org.jboss.resteasy.test.TestPortProvider.getPort;

import java.util.List;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.HttpContextProvider;
import org.jboss.resteasy.plugins.server.embedded.SimpleSecurityDomain;
import org.jboss.resteasy.plugins.server.sun.http.HttpServerContainer;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BasicAuthTest {
    private static Dispatcher dispatcher;

    @Path("/secured")
    public interface BaseProxy {
        @GET
        String get();

        @GET
        @Path("/authorized")
        String getAuthorized();

        @GET
        @Path("/deny")
        String deny();

        @GET
        @Path("/failure")
        List<String> getFailure();
    }

    @Path("/secured")
    public static class BaseResource {
        @GET
        @Path("/failure")
        @RolesAllowed("admin")
        public List<String> getFailure() {
            return null;
        }

        @GET
        public String get(@Context SecurityContext ctx) {
            //         System.out.println("********* IN SECURE CLIENT");
            if (!ctx.isUserInRole("admin")) {
                //            System.out.println("NOT IN ROLE!!!!");
                throw new WebApplicationException(403);
            }
            return "hello";
        }

        @GET
        @Path("/authorized")
        @RolesAllowed("admin")
        public String getAuthorized() {
            return "authorized";
        }

        @GET
        @Path("/deny")
        @DenyAll
        public String deny() {
            return "SHOULD NOT BE REACHED";
        }
    }

    @Path("/secured2")
    public static class BaseResource2 {
        public String get(@Context SecurityContext ctx) {
            //         System.out.println("********* IN SECURE CLIENT");
            if (!ctx.isUserInRole("admin")) {
                //            System.out.println("NOT IN ROLE!!!!");
                throw new WebApplicationException(403);
            }
            return "hello";
        }

        @GET
        @Path("/authorized")
        @RolesAllowed("admin")
        public String getAuthorized() {
            return "authorized";
        }

    }

    @BeforeAll
    public static void before() throws Exception {
        SimpleSecurityDomain domain = new SimpleSecurityDomain();
        String[] roles = { "admin" };
        String[] basic = { "user" };
        domain.addUser("bill", "password", roles);
        domain.addUser("mo", "password", basic);
        dispatcher = HttpServerContainer.start("", domain).getDispatcher();
        dispatcher.getRegistry().addPerRequestResource(BaseResource.class);
        dispatcher.getRegistry().addPerRequestResource(BaseResource2.class);
    }

    @AfterAll
    public static void after() throws Exception {
        HttpServerContainer.stop();
    }

    @Test
    public void testProxy() throws Exception {
        BasicCredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials("bill", "password"));
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(cp).build();
        ClientHttpEngine engine = createAuthenticatingEngine(httpClient);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
        BaseProxy proxy = target.proxy(BaseProxy.class);
        String val = proxy.get();
        Assertions.assertEquals(val, "hello");
        val = proxy.getAuthorized();
        Assertions.assertEquals(val, "authorized");
    }

    @Test
    public void testProxyFailure() throws Exception {
        BaseProxy proxy = createProxy(BaseProxy.class, TestPortProvider.generateBaseUrl());
        try {
            proxy.getFailure();
        } catch (NotAuthorizedException e) {
            Assertions.assertEquals(e.getResponse().getStatus(), 401);
            e.getResponse().close();
        }
    }

    @Test
    public void testSecurity() throws Exception {
        BasicCredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials("bill", "password"));
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(cp).build();
        ClientHttpEngine engine = createAuthenticatingEngine(httpClient);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();

        {
            Builder request = client.target(generateURL("/secured")).request();
            Response response = request.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("hello", response.readEntity(String.class));
        }

        {
            Builder request = client.target(generateURL("/secured/authorized")).request();
            Response response = request.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("authorized", response.readEntity(String.class));
        }

        {
            Builder request = client.target(generateURL("/secured/deny")).request();
            Response response = request.get();
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        }
    }

    /**
     * RESTEASY-579
     *
     * Found 579 bug when doing 575 so the test is here out of laziness
     *
     * @throws Exception
     */
    @Test
    public void test579() throws Exception {
        BasicCredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(new AuthScope(AuthScope.ANY), new UsernamePasswordCredentials("bill", "password"));
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(cp).build();
        ClientHttpEngine engine = createAuthenticatingEngine(httpClient);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();
        Response response = client.target(generateURL("/secured2")).request().get();
        Assertions.assertEquals(404, response.getStatus());
        response.close();
    }

    @Test
    public void testSecurityFailure() throws Exception {
        BasicCredentialsProvider cp = new BasicCredentialsProvider();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(cp).build();

        {
            HttpGet method = new HttpGet(generateURL("/secured"));
            HttpResponse response = httpClient.execute(method);
            Assertions.assertEquals(401, response.getStatusLine().getStatusCode());
            EntityUtils.consume(response.getEntity());
        }

        ClientHttpEngine engine = createAuthenticatingEngine(httpClient);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build();

        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bill", "password");
            cp.setCredentials(new AuthScope(AuthScope.ANY), credentials);
            Response response = client.target(generateURL("/secured/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("authorized", response.readEntity(String.class));
        }

        {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("mo", "password");
            cp.setCredentials(new AuthScope(AuthScope.ANY), credentials);
            Response response = client.target(generateURL("/secured/authorized")).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
            response.close();
        }
    }

    /**
     * Create a ClientExecutor which does preemptive authentication.
     */

    private static ClientHttpEngine createAuthenticatingEngine(CloseableHttpClient client) {
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();

        // Create ClientExecutor.
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(client, new HttpContextProvider() {
            @Override
            public HttpContext getContext() {
                // Generate BASIC scheme object and add it to the local auth cache
                BasicScheme basicAuth = new BasicScheme();
                HttpHost targetHost = new HttpHost(getHost(), getPort());
                authCache.put(targetHost, basicAuth);

                // Add AuthCache to the execution context
                BasicHttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
                return localContext;
            }
        });
        return engine;
    }
}
