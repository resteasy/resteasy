package org.jboss.resteasy.embedded.test.interceptor;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.embedded.test.AbstractBootstrapTest;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientRequestFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionClientResponseFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerRequestFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilter1;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilter2;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilter3;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilterMax;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionContainerResponseFilterMin;
import org.jboss.resteasy.embedded.test.interceptor.resource.PriorityExecutionResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpSince RESTEasy 4.1.0
 * @tpTestCaseDetails Regression test for RESTEASY-1294
 */
public class PriorityExecutionTest extends AbstractBootstrapTest {
    public static volatile Queue<String> interceptors = new ConcurrentLinkedQueue<String>();
    public static Logger logger = Logger.getLogger(PriorityExecutionTest.class);
    private static final String WRONG_ORDER_ERROR_MSG = "Wrong order of interceptor execution";

    @BeforeEach
    public void setup() throws Exception {
        Set<Class<?>> actualProviderClassList = new LinkedHashSet<>();
        actualProviderClassList.add(PriorityExecutionResource.class);
        actualProviderClassList.add(PriorityExecutionContainerResponseFilter2.class);
        actualProviderClassList.add(PriorityExecutionContainerResponseFilter1.class);
        actualProviderClassList.add(PriorityExecutionContainerResponseFilter3.class);
        actualProviderClassList.add(PriorityExecutionContainerResponseFilterMin.class);
        actualProviderClassList.add(PriorityExecutionContainerResponseFilterMax.class);
        actualProviderClassList.add(PriorityExecutionContainerRequestFilter2.class);
        actualProviderClassList.add(PriorityExecutionContainerRequestFilter1.class);
        actualProviderClassList.add(PriorityExecutionContainerRequestFilter3.class);
        actualProviderClassList.add(PriorityExecutionContainerRequestFilterMin.class);
        actualProviderClassList.add(PriorityExecutionContainerRequestFilterMax.class);
        final Application application = new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return actualProviderClassList;
            }
        };

        start(application);
    }

    /**
     * @tpTestDetails Check order of client and server filters
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void testPriority() throws Exception {
        client.register(PriorityExecutionClientResponseFilter3.class);
        client.register(PriorityExecutionClientResponseFilter1.class);
        client.register(PriorityExecutionClientResponseFilter2.class);
        client.register(PriorityExecutionClientResponseFilterMin.class);
        client.register(PriorityExecutionClientResponseFilterMax.class);
        client.register(PriorityExecutionClientRequestFilter3.class);
        client.register(PriorityExecutionClientRequestFilter1.class);
        client.register(PriorityExecutionClientRequestFilter2.class);
        client.register(PriorityExecutionClientRequestFilterMin.class);
        client.register(PriorityExecutionClientRequestFilterMax.class);

        Response response = client.target(generateURL("/test")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("test", response.getEntity(), "Wrong content of response");

        // client filters
        Assertions.assertEquals("PriorityExecutionClientRequestFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientRequestFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);

        // server filters
        Assertions.assertEquals("PriorityExecutionContainerRequestFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerRequestFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionContainerResponseFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);

        // client filters
        Assertions.assertEquals("PriorityExecutionClientResponseFilterMax", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilter3", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilter2", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilter1", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
        Assertions.assertEquals("PriorityExecutionClientResponseFilterMin", interceptors.poll(), WRONG_ORDER_ERROR_MSG);
    }
}
