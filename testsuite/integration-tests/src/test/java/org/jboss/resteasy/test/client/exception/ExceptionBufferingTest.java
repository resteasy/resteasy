package org.jboss.resteasy.test.client.exception;

import static org.junit.Assert.fail;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.test.client.exception.resource.ExceptionBufferingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-981
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExceptionBufferingTest {

    protected static final Logger logger = LogManager.getLogger(ExceptionBufferingTest.class.getName());

    private static final String DEPLOYMENT_TRUE = "buffer";
    private static final String DEPLOYMENT_FALSE = "nobuffer";
    private static final String DEPLOYMENT_DEFAULT = "default";

    protected static ResteasyClient client;


    @Deployment(name = DEPLOYMENT_TRUE)
    public static Archive<?> deployTrue() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_TRUE);
        Map<String, String> params = new HashMap<>();
        params.put("resteasy.buffer.exception.entity", "true");
        return TestUtil.finishContainerPrepare(war, params, ExceptionBufferingResource.class);
    }

    @Deployment(name = DEPLOYMENT_FALSE)
    public static Archive<?> deployFalse() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_FALSE);
        Map<String, String> params = new HashMap<>();
        params.put("resteasy.buffer.exception.entity", "false");
        return TestUtil.finishContainerPrepare(war, params, ExceptionBufferingResource.class);
    }

    @Deployment(name = DEPLOYMENT_DEFAULT)
    public static Archive<?> deployDefault() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_DEFAULT);
        client = new ResteasyClientBuilder().build();
        return TestUtil.finishContainerPrepare(war, null, ExceptionBufferingResource.class);
    }

    @AfterClass
    public static void init() throws Exception {
        client.close();
    }


    /**
     * @tpTestDetails Test default value of resteasy.buffer.exception.entity property
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBufferedResponseDefault() throws Exception {
        Response response = null;

        try {
            ResteasyWebTarget target = client.target(PortProviderUtil.generateURL("/test", DEPLOYMENT_DEFAULT));
            Invocation invocation = target.request().buildGet();
            response = invocation.invoke();
            logger.info("status: " + response.getStatus());
            String s = ClientInvocation.extractResult(new GenericType<String>(String.class), response, null);
            fail("Was expecting an exception: " + s);
        } catch (Exception e) {
            logger.info("caught: " + e);
            String entity = response.readEntity(String.class);
            logger.info("exception entity: " + entity);
            Assert.assertEquals("Wrong response content", "test", entity);
        }
    }

    /**
     * @tpTestDetails Test false value of resteasy.buffer.exception.entity property
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBufferedResponseFalse() throws Exception {
        Response response = null;

        try {
            ResteasyWebTarget target = client.target(PortProviderUtil.generateURL("/test", DEPLOYMENT_FALSE));
            Invocation invocation = target.request().buildGet();
            response = invocation.invoke();
            logger.info("status: " + response.getStatus());
            String s = ClientInvocation.extractResult(new GenericType<String>(String.class), response, null);
            fail("Was expecting an exception: " + s);
        } catch (Exception e) {
            logger.info("caught: " + e);
            try {
                String s = response.readEntity(String.class);
                fail("Was expecting a second exception: " + s);
            } catch (ProcessingException e1) {
                logger.info("and caught: " + e1);
                Assert.assertTrue("Wrong exception thrown", e1.getCause() instanceof IOException);
                Assert.assertEquals("Attempted read on closed stream.", e1.getCause().getMessage());
            } catch (Exception e1) {
                fail("Was expecting a ProcessingException instead of " + e1);
            }
        }
    }

    /**
     * @tpTestDetails Test true value of resteasy.buffer.exception.entity property
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBufferedResponseTrue() throws Exception {
        Response response = null;

        try {
            ResteasyWebTarget target = client.target(PortProviderUtil.generateURL("/test", DEPLOYMENT_TRUE));
            Invocation invocation = target.request().buildGet();
            response = invocation.invoke();
            logger.info("status: " + response.getStatus());
            String s = ClientInvocation.extractResult(new GenericType<String>(String.class), response, null);
            fail("Was expecting an exception: " + s);
        } catch (Exception e) {
            logger.info("caught: " + e);
            String entity = response.readEntity(String.class);
            logger.info("exception entity: " + entity);
            Assert.assertEquals("Wrong responce content", "test", entity);
        }
    }
}
