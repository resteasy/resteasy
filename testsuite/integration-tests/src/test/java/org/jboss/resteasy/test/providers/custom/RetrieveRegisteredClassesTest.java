package org.jboss.resteasy.test.providers.custom;

import java.io.IOException;
import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RetrieveRegisteredClassesTest {

    @Path("/testResource")
    @Produces(MediaType.APPLICATION_XML)
    public static final class TestResource {

        @GET
        public String get() {
            return TestResource.class.getName();
        }

    }

    private static class MyFilter implements ClientRequestFilter {

        // To discard empty constructor
        private MyFilter(final Object value) {
        }

        @Override
        public void filter(ClientRequestContext clientRequestContext) throws IOException {
        }

    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RetrieveRegisteredClassesTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TestResource.class);
    }

    @Test
    public void test() {

        Client client = ClientBuilder.newClient();
        try {
            String uri = PortProviderUtil
                    .generateURL("/testResource", RetrieveRegisteredClassesTest.class.getSimpleName());
            MyFilter myFilter = new MyFilter(new Object());

            WebTarget firstWebTarget = client.target(uri).register(myFilter);
            String firstResult = firstWebTarget.request(MediaType.APPLICATION_XML).get(String.class);
            Configuration firstWebTargetConfiguration = firstWebTarget.getConfiguration();
            Set<Class<?>> classes = firstWebTargetConfiguration.getClasses();
            Set<Object> instances = firstWebTargetConfiguration.getInstances();
            Assertions.assertFalse(classes.contains(MyFilter.class));
            Assertions.assertTrue(instances.contains(myFilter));

            WebTarget secondWebTarget = client.target(uri);
            Configuration secondWebTargetConfiguration = secondWebTarget.getConfiguration();
            for (Class<?> classz : classes) {
                if (!secondWebTargetConfiguration.isRegistered(classz)) {
                    secondWebTarget.register(classz);
                }
            }
            for (Object instance : instances) {
                if (!secondWebTargetConfiguration.isRegistered(instance.getClass())) {
                    secondWebTarget.register(instance);
                }
            }
            String secondeResult = secondWebTarget.request(MediaType.APPLICATION_XML).get(String.class);
            classes = secondWebTargetConfiguration.getClasses();
            instances = secondWebTargetConfiguration.getInstances();
            Assertions.assertFalse(classes.contains(MyFilter.class));
            Assertions.assertTrue(instances.contains(myFilter));
            Assertions.assertEquals(firstResult, secondeResult);
        } finally {
            client.close();
        }

    }

}
