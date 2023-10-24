package org.jboss.resteasy.links.test;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.security.Principal;

import jakarta.ws.rs.client.ClientBuilder;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.HttpContextProvider;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.embedded.SimplePrincipal;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class TestSecureLinks {
    private static NettyJaxrsServer server;
    private static Dispatcher dispatcher;

    @BeforeAll
    public static void beforeClass() throws Exception {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        server.setSecurityDomain(new SecurityDomain() {

            public Principal authenticate(String username, String password) throws SecurityException {
                return new SimplePrincipal(username);
            }

            public boolean isUserInRole(Principal username, String role) {
                return username.getName().equals(role);
            }

        });

        ResteasyDeployment deployment = server.getDeployment();
        deployment.start();
        dispatcher = deployment.getDispatcher();
        server.start();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        server.stop();
        server = null;
        dispatcher = null;
    }

    private String url;
    private BookStoreService client;
    private CloseableHttpClient httpClient;
    private CredentialsProvider cp;

    @ParameterizedTest
    @ArgumentsSource(SecureBookClassProvider.class)
    public void testSecureLinksAdmin(Class resourceType, String type) throws Exception {
        POJOResourceFactory noDefaults = new POJOResourceFactory(new ResourceBuilder(), resourceType);
        dispatcher.getRegistry().addResourceFactory(noDefaults);
        url = generateBaseUrl();

        cp = new BasicCredentialsProvider();
        httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(cp).build();
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient, new HttpContextProvider() {
            @Override
            public HttpContext getContext() {
                // Configure HttpClient to authenticate preemptively
                // by prepopulating the authentication data cache.
                // 1. Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // 2. Generate BASIC scheme object and add it to the local auth cache
                BasicScheme basicAuth = new BasicScheme();
                authCache.put(getHttpHost(url), basicAuth);
                // 3. Add AuthCache to the execution context
                BasicHttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
                return localContext;
            }
        });
        ResteasyWebTarget target = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(engine).build().target(url);
        client = target.proxy(BookStoreService.class);
        Book book = null;
        switch (type) {
            case "admin":
                cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "asd"));
                book = client.getBookXML("foo");
                checkBookLinks1(url, book, "add", "update", "list", "self", "remove");
                break;
            case "power-user":
                cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("power-user", "asd"));
                book = client.getBookXML("foo");
                checkBookLinks1(url, book, "add", "update", "list", "self");
                break;
            case "user":
                cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "asd"));
                book = client.getBookXML("foo");
                checkBookLinks1(url, book, "list", "self");
                break;
        }
        dispatcher.getRegistry().removeRegistrations(resourceType);
        cp = null;
    }

    private void checkBookLinks1(String url, Book book, String... expectedLinks) {
        Assertions.assertNotNull(book);
        Assertions.assertEquals("foo", book.getTitle());
        Assertions.assertEquals("bar", book.getAuthor());
        RESTServiceDiscovery links = book.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(expectedLinks.length, links.size());
        for (String expectedLink : expectedLinks) {
            Assertions.assertNotNull(links.getLinkForRel(expectedLink));
        }
    }

    private HttpHost getHttpHost(String url) {
        int i1 = url.indexOf(':');
        String scheme = url.substring(0, i1);
        int i2 = url.indexOf(':', i1 + 1);
        String host = url.substring(i1 + 3, i2);
        int i3 = url.indexOf('/', i2 + 1);
        String port = i3 == -1 ? url.substring(i2 + 1)
                : url.substring(i2 + 1, i3);
        return new HttpHost(host, Integer.valueOf(port), scheme);
    }
}
