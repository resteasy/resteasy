package org.jboss.resteasy.test.nextgen.properties;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * Simple smoke test
 *
 */
public class GetPropertiesTest {

    private static HttpServer server;
    private static HttpContextBuilder contextBuilder;
    private static boolean propertyOk;

    @BeforeClass
    public static void before() throws Exception {
        server = HttpServer.create(new InetSocketAddress(8081), 1);
        contextBuilder = new HttpContextBuilder();
		final Map<String, Object> applicationProperties = new HashMap<>();
		applicationProperties.put("Prop1", "Value1");
		applicationProperties.put("Prop2", "Value2");
        Application application = new Application() {
            @Override
            public Map<String, Object> getProperties() {
				return applicationProperties;
            }

            @Override
            public Set<Class<?>> getClasses() {
                Set<Class<?>> classes = new HashSet();
                return classes;
            }

            @Override
            public Set<Object> getSingletons() {
                return Collections.<Object>singleton(new Feature() {

                    @Override
                    public boolean configure(FeatureContext featureContext) {
						propertyOk = applicationProperties.equals(featureContext.getConfiguration().getProperties());
						return propertyOk;
                    }
                });
            }

        };
        contextBuilder.getDeployment().setApplication(application);
        HttpContext context = contextBuilder.bind(server);
        server.start();

    }

    @AfterClass
    public static void after() throws Exception {
        contextBuilder.cleanup();
        server.stop(0);
    }

    @Test
    public void testCheckProperty() throws Exception {
        Assert.assertEquals(true, propertyOk);
    }
}
