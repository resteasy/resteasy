package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryStrParamUnmarshaller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-584
 * @tpSince RESTEasy 3.0.16
 */
public class ProviderFactoryTest {

    private ResteasyProviderFactory factory;

    @Before
    public void createBean() {
        factory = new ResteasyProviderFactory();
    }

    /**
     * @tpTestDetails Basic check for ResteasyProviderFactory class.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldReturnStringParameterUnmarshallerAddedForType() {
        factory.addStringParameterUnmarshaller(ProviderFactoryStrParamUnmarshaller.class);
        assertNotNull("Null StringParameterUnmarshaller object", factory.createStringParameterUnmarshaller(Date.class));
    }

    /**
     * @tpTestDetails Regression test for JBEAP-4706
     *                Test whether the priority is supplied to the container request filter registry.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testRegisterProviderInstancePriorityContainerRequestFilter() throws Exception {
        ContainerRequestFilter requestFilter = new ContainerRequestFilter() {
            public void filter(ContainerRequestContext requestContext) {
            }
        };
        this.testRegisterProviderInstancePriority(requestFilter, factory.getContainerRequestFilterRegistry());
    }

    /**
     * @tpTestDetails Regression test for JBEAP-4706
     *                Test whether the priority is supplied to the container response filter registry.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testRegisterProviderInstancePriorityContainerResponseFilter() throws Exception {
        ContainerResponseFilter responseFilter = new ContainerResponseFilter() {
            public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
            }
        };
        this.testRegisterProviderInstancePriority(responseFilter, factory.getContainerResponseFilterRegistry());
    }

    /**
     * Generic helper method for RESTEASY-1311 cases, because the test logic is the same.
     * Unfortunately, there seems to be no public accessors for the properties we need,
     * so we have to resort to using reflection to check the right priority setting.
     */
    private void testRegisterProviderInstancePriority(Object filter, Object registry) throws Exception {
        int priorityOverride = Priorities.USER + 1;
        factory.registerProviderInstance(filter, null, priorityOverride, false);

        Field interceptorsField = registry.getClass().getSuperclass().getDeclaredField("interceptors");
        interceptorsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<JaxrsInterceptorRegistry.InterceptorFactory> interceptors = (List<JaxrsInterceptorRegistry.InterceptorFactory>) interceptorsField.get(registry);

        Field orderField = interceptors.get(0).getClass().getSuperclass().getDeclaredField("order");
        orderField.setAccessible(true);
        int order = (Integer) orderField.get(interceptors.get(0));
        Assert.assertEquals(priorityOverride, order);
    }

}
