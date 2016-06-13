package org.jboss.resteasy.test.resteasy903;

import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy903.ForwardServlet;
import org.jboss.resteasy.resteasy903.TestServlet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
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
 * Copyright Apr 5, 2014
 */
@RunWith(Arquillian.class)
public class UndertowTest
{
   private static final Logger log = LoggerFactory.getLogger(UndertowTest.class);

	@Deployment
	public static Archive<?> createTestArchive()
	{
	   WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-903.war")
	         .addClasses(TestServlet.class, ForwardServlet.class)
	         .setManifest("MANIFEST.MF")
	         .addAsWebInfResource("web_undertow.xml", "web.xml")
	         ;
	   System.out.println(war.toString(true));
	   return war;
	}
	
	@Test
	public void testUndertow() throws Exception
	{
	   log.info("starting testUndertow()");
	   URL url = new URL("http://localhost:8080/RESTEASY-903/test");
	   HttpURLConnection conn = HttpURLConnection.class.cast(url.openConnection());
	   conn.connect();
	   System.out.println("status: " + conn.getResponseCode());
	   byte[] b = new byte[16];
	   conn.getInputStream().read(b);
	   System.out.println("result: " + new String(b));
	   Assert.assertEquals(200, conn.getResponseCode());
	   conn.disconnect();
	}
}
