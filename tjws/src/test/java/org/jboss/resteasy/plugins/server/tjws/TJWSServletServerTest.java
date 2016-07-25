package org.jboss.resteasy.plugins.server.tjws;

import org.junit.Assert;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Invocation.Builder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class TJWSServletServerTest
{

   TJWSServletServer server = null;

   @Before
   public void setup()
   {
      server = new TJWSServletServer();
      server.setPort(TestPortProvider.getPort());
   }

   @After
   public void finish()
   {
      server.stop();
   }

   @Test
   public void testResource() throws Exception
   {
      server.addServlet("/hello", new HttpServlet()
      {
         private static final long serialVersionUID = -4176523779912453903L;

         @Override
         protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
         {
            resp.getWriter().write("world");
         }
      });
      server.start();

      Builder request = TestPortProvider.createTarget("/hello").request();
      Assert.assertEquals("world", request.get().readEntity(String.class));
   }

   @Test
   /**
    * Test to make sure that file system mapping works.
    */
   public void testFile() throws Exception
   {
      URL root = getClass().getClassLoader().getResource(".");
      URI uri = new URI(root.getFile());

      server.server.addFileMapping("/", new File(uri.getPath()));
      server.start();

      checkText("/test.txt", "Hello, World!");
      checkText("/test.html", "<html><body>Hello, World!</body></html>");
   }

   private void checkText(String uri, final String text) throws Exception
   {
      Builder request = TestPortProvider.createTarget(uri).request();
      Assert.assertEquals(text, request.get().readEntity(String.class));
   }


}
