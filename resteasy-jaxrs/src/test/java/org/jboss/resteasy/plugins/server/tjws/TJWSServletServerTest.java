package org.jboss.resteasy.plugins.server.tjws;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
   public void finish(){
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

      ClientRequest request = TestPortProvider.createClientRequest("/hello");
      Assert.assertEquals("world", request.get(String.class).getEntity());
   }

   @Test
   /**
    * Test to make sure that file system mapping works.
    */
   public void testFile() throws Exception
   {
      URL root = getClass().getClassLoader().getResource(".");

      server.server.addFileMapping("/", new File(root.getFile()));
      server.start();

      checkText("/test.txt", "Hello, World!");
      checkText("/test.html", "<html><body>Hello, World!</body></html>");
   }

   private void checkText(String uri, final String text) throws Exception
   {
      ClientRequest request = TestPortProvider.createClientRequest(uri);
      Assert.assertEquals(text, request.get(String.class).getEntity());
   }
}
