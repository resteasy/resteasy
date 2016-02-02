package org.jboss.resteasy.test.resteasy1273;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.resteasy1273.TestApplication;
import org.jboss.resteasy.resteasy1273.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RESTEASY-1273
 * 
 * Test connection cleanup
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ApacheHttpClient4Test {

    private Class clazz = URLConnectionEngine.class;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-1273.war")
                .addClasses(TestApplication.class)
                .addClasses(TestResource.class)
                .addAsWebInfResource("1273/web.xml");
        System.out.println(war.toString(true));
        return war;
    }

    private AtomicLong counter = new AtomicLong();


    private String generateBaseUrl() {
        return "http://localhost:8080/" + ApacheHttpClient4Test.class.getSimpleName();
    }

    @Test
    public void testConnectionCleanupErrorGC() throws Exception {
        final ResteasyClient client = createEngine();
        final TestResource proxy = client.target(generateBaseUrl()).proxy(TestResource.class);

        System.out.println("calling proxy");
        callProxy(proxy);
        System.gc();
    }

    @Test
    public void testConnectionCleanupErrorNoGC() throws Exception {
        final ResteasyClient client = createEngine();
        final TestResource proxy = client.target(generateBaseUrl()).proxy(TestResource.class);

        System.out.println("calling proxy");
        String str = null;
        try {
            str = proxy.error();
            Assert.fail("expecting 404");
        } catch (NotFoundException e) {
        	System.out.println("status: " + e.getResponse().getStatus());
            Assert.assertEquals(e.getResponse().getStatus(), 404);
            e.getResponse().close();
            counter.incrementAndGet();
        }
    }

    private void callProxy(TestResource proxy) {
        String str = null;
        try {
            str = proxy.error();
        } catch (NotFoundException e) {
        	System.out.println("status: " + e.getResponse().getStatus());
            Assert.assertEquals(e.getResponse().getStatus(), 404);
            e.getResponse().close();
            counter.incrementAndGet();
        }
    }


    private ResteasyClient createEngine() {
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 3);
        ConnManagerParams.setTimeout(params, 5000);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(cm, params);

        final ClientHttpEngine executor;

        if (clazz.isAssignableFrom(ApacheHttpClient4Engine.class)) {
            executor = new ApacheHttpClient4Engine(httpClient);
        } else {
            executor = new URLConnectionEngine();
        }


        ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();
        return client;
    }

    private void runit(Client client, boolean release) {
        WebTarget target = client.target(generateBaseUrl() + "/test");
        try {
            System.out.println("get");
            Response response = target.request().get();
            Assert.assertEquals(200, response.getStatus());
            //Assert.assertEquals("hello world", response.getEntity(String.class));
            System.out.println("ok");
            if (release) response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        counter.incrementAndGet();
    }
}
