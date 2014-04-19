package org.jboss.resteasy.test.resteasy903;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy903.ForwardServlet;
import org.jboss.resteasy.resteasy903.TestApplication;
import org.jboss.resteasy.resteasy903.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTEASY-903
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 11, 2014
 */
@RunWith(Arquillian.class)
public class FilterTest
{
   private static final Logger log = LoggerFactory.getLogger(FilterTest.class);

	@Deployment
	public static Archive<?> createTestArchive()
	{
	   WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-903.war")
	         .addClasses(TestApplication.class, TestResource.class, ForwardServlet.class)
	         .addAsWebInfResource("test.html")
	         .addAsWebInfResource("web.xml", "web.xml")
	         ;
	   System.out.println(war.toString(true));
	   return war;
	}
	
	@Test
	public void testFilter() throws Exception
	{
	   log.info("starting testFilter()");
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-903/test/dispatch/dynamic");
      ClientResponse<?> response = request.get();
      String answer = response.getEntity(String.class);
      log.info("Status: " + response.getStatus());
      log.info("result:\r" + response.getEntity(String.class));
      assertEquals(200, response.getStatus());
	}
}
