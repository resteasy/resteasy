package org.jboss.resteasy.microprofile.client.headers;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.CoreMatchers;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientHeaderFillingTest {
    private static final String HEADER_NAME = "GENERATED_HEADER";

    private static UndertowJaxrsServer server;
    private static WeldContainer container;

    @BeforeClass
    public static void init() {
        Weld weld = new Weld();
        weld.addBeanClass(HeaderPassingResource.class);
        weld.addBeanClass(HeaderSendingClient.class);
        weld.addBeanClass(ClientInvokingBean.class);
        container = weld.initialize();
        server = new UndertowJaxrsServer().start();
        server.deploy(MyApp.class);
    }

    @Test
    public void checkIfFillerFactoryWithHigherPrioritySelected() {
        List<String> result = container.select(ClientInvokingBean.class).get().getHeaders();
        Assert.assertThat(result, CoreMatchers.hasItems("high", "prio"));
    }

    @AfterClass
    public static void stop() {
        server.stop();
        container.shutdown();
    }

    @Path("/")
    @RegisterRestClient(baseUri="http://localhost:8081")
    public interface HeaderSendingClient {
        @GET
        @ClientHeaderParam(name = HEADER_NAME, value = "{someMethod}")
        String headerValues();

        default List<String> someMethod() {
            return Arrays.asList("foo", "bar");
        }
    }

    @Path("/")
    public static class HeaderPassingResource {
        @GET
        public String headerValues(@HeaderParam(HEADER_NAME) List<String> headers) {
            return String.join(",", headers);
        }
    }

    @ApplicationScoped
    public static class ClientInvokingBean {
        @RestClient
        @Inject
        private HeaderSendingClient client;

        public List<String> getHeaders() {
            String headers = client.headerValues();
            return Arrays.asList(headers.split(","));
        }
    }

    @ApplicationPath("")
    public static class MyApp extends Application {

        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<>();
            classes.add(HeaderPassingResource.class);
            return classes;
        }
    }
}
