package org.jboss.resteasy.test.asynch.resource;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.asynch.AsynchBasicTest;
import org.junit.Assert;

@Path("/")
public class AsynchBasicResource {

    protected final Logger logger = Logger.getLogger(AsynchBasicResource.class.getName());

    @Context
    private ServletConfig config;

    @Context
    private ServletContext context;

    @POST
    public String post(String content) throws Exception {
        logger.info("in post");
        Assert.assertNotNull(config);
        Assert.assertNotNull(context);
        logger.info("Asserts passed");
        config.getServletContext();
        context.getMajorVersion();
        logger.info("Called injected passed");

        Thread.sleep(1500);
        AsynchBasicTest.latch.countDown();

        return content;
    }

    @PUT
    public void put(String content) throws Exception {
        Assert.assertNotNull(config);
        Assert.assertNotNull(context);
        config.getServletContext();
        context.getMajorVersion();
        Assert.assertEquals("content", content);
        Thread.sleep(500);
        AsynchBasicTest.latch.countDown();
    }
}
