package org.jboss.resteasy.test.interception;

import org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.jaxrs.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.interception.resource.PriorityClientRequestFilter1;
import org.jboss.resteasy.test.interception.resource.PriorityClientRequestFilter2;
import org.jboss.resteasy.test.interception.resource.PriorityClientRequestFilter3;
import org.jboss.resteasy.test.interception.resource.PriorityClientResponseFilter1;
import org.jboss.resteasy.test.interception.resource.PriorityClientResponseFilter2;
import org.jboss.resteasy.test.interception.resource.PriorityClientResponseFilter3;
import org.jboss.resteasy.test.interception.resource.PriorityContainerResponseFilter1;
import org.jboss.resteasy.test.interception.resource.PriorityContainerResponseFilter2;
import org.jboss.resteasy.test.interception.resource.PriorityContainerResponseFilter3;
import org.junit.Test;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerResponseFilter;

import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Interception tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Check functionality of Priority annotation on filter classes. Use more classes with different value in Priority annotation.
 * @tpSince RESTEasy 3.0.16
 */
public class PriorityTest {

    private static final String ERROR_MESSAGE = "RESTEasy uses filter in wrong older";
    /**
     * @tpTestDetails Test for classes implements ContainerResponseFilter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPriority() throws Exception {
        ContainerResponseFilterRegistry containerResponseFilterRegistry = new ContainerResponseFilterRegistry(new ResteasyProviderFactory());
        ClientResponseFilterRegistry clientResponseFilterRegistry = new ClientResponseFilterRegistry(new ResteasyProviderFactory());
        JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry = new JaxrsInterceptorRegistry<ClientRequestFilter>(new ResteasyProviderFactory(), ClientRequestFilter.class);

        containerResponseFilterRegistry.registerClass(PriorityContainerResponseFilter2.class);
        containerResponseFilterRegistry.registerClass(PriorityContainerResponseFilter1.class);
        containerResponseFilterRegistry.registerClass(PriorityContainerResponseFilter3.class);

        ContainerResponseFilter[] containerResponseFilters = containerResponseFilterRegistry.postMatch(null, null);
        assertTrue(ERROR_MESSAGE, containerResponseFilters[0] instanceof PriorityContainerResponseFilter3);
        assertTrue(ERROR_MESSAGE, containerResponseFilters[1] instanceof PriorityContainerResponseFilter2);
        assertTrue(ERROR_MESSAGE, containerResponseFilters[2] instanceof PriorityContainerResponseFilter1);

        clientResponseFilterRegistry.registerClass(PriorityClientResponseFilter3.class);
        clientResponseFilterRegistry.registerClass(PriorityClientResponseFilter1.class);
        clientResponseFilterRegistry.registerClass(PriorityClientResponseFilter2.class);

        ClientResponseFilter[] clientResponseFilters = clientResponseFilterRegistry.postMatch(null, null);
        assertTrue(ERROR_MESSAGE, clientResponseFilters[0] instanceof PriorityClientResponseFilter3);
        assertTrue(ERROR_MESSAGE, clientResponseFilters[1] instanceof PriorityClientResponseFilter2);
        assertTrue(ERROR_MESSAGE, clientResponseFilters[2] instanceof PriorityClientResponseFilter1);

        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter3.class);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter1.class);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter2.class);

        ClientRequestFilter[] clientRequestFilters = clientRequestFilterRegistry.postMatch(null, null);
        assertTrue(ERROR_MESSAGE, clientRequestFilters[0] instanceof PriorityClientRequestFilter1);
        assertTrue(ERROR_MESSAGE, clientRequestFilters[1] instanceof PriorityClientRequestFilter2);
        assertTrue(ERROR_MESSAGE, clientRequestFilters[2] instanceof PriorityClientRequestFilter3);

    }

    /**
     * @tpTestDetails Test for classes implements ClientRequestFilter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPriorityOverride() {
        JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry = new JaxrsInterceptorRegistry<ClientRequestFilter>(new ResteasyProviderFactory(), ClientRequestFilter.class);

        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter3.class, 100);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter1.class, 200);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter2.class, 300);

        ClientRequestFilter[] clientRequestFilters = clientRequestFilterRegistry.postMatch(null, null);
        assertTrue(ERROR_MESSAGE, clientRequestFilters[0] instanceof PriorityClientRequestFilter3);
        assertTrue(ERROR_MESSAGE, clientRequestFilters[1] instanceof PriorityClientRequestFilter1);
        assertTrue(ERROR_MESSAGE, clientRequestFilters[2] instanceof PriorityClientRequestFilter2);
    }
}
