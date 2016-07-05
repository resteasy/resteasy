package org.jboss.resteasy.test.asynch.resource;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.test.asynch.AsynchBasicTest;
import org.junit.Assert;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/")
public class AsynchBasicResource {

    private static Logger logger = Logger.getLogger(AsynchBasicResource.class);

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
        logger.info("IN PUT!!!!");
        Assert.assertNotNull(config);
        Assert.assertNotNull(context);
        logger.info("Asserts passed");
        config.getServletContext();
        context.getMajorVersion();
        logger.info("Called injected passed");
        Assert.assertEquals("content", content);
        Thread.sleep(500);
        logger.info("******* countdown ****");
        AsynchBasicTest.latch.countDown();
        logger.info("******* countdown complete ****");
    }
}
