package org.jboss.resteasy.test.asynch.resource;

import org.jboss.logging.Logger;
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
