package org.jboss.resteasy.test.tjws;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for RESTEASY-602.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: $
 */
public class ChunkedEmptyEntityTest
{
   private static final Logger LOG = Logger.getLogger(ChunkedEmptyEntityTest.class);
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }
   
   @Test
   public void testContinue() throws Exception
   {
      _run_test("PUT", "/continue", "100");
   }
   
   @Test
   public void testHead() throws Exception
   {
      _run_test("HEAD", "/head", "204");
   }
   
   @Test
   public void testNoContent() throws Exception
   {
      _run_test("PUT", "/nocontent", "204");
   }
   
   @Test
   public void testNotModified() throws Exception
   {
      _run_test("GET", "/notmodified", "304");
   }
   
   void _run_test(String method, String path, String status) throws Exception
   {
	   // Solicit a reply with response code 204.
	   Socket s = new Socket("localhost", 8081);
	   OutputStream os = s.getOutputStream();
	   writeString(os, method + " " + path + " HTTP/1.1");
	   writeString(os, "Content-Length: 11");
	   writeString(os, "Content-Type: text/plain");
	   writeString(os, "Host: localhost:8081");
	   writeString(os, "");
	   os.write("hello world".getBytes());
	   os.flush();
	   
	   // Verify response code is correct and that the message 
	   // 1. has no "transfer-encoding" header, and
	   // 2. consists of status line and headers but no chunks.
	   InputStream is = s.getInputStream();
	   String line = readLine(is);
	   LOG.info("<<" + line);
	   assertTrue(line.contains(status));
	   line = readLine(is);
	   while (line != null && is.available() > 0)
	   {
		   LOG.info("<<" + line);
		   int i = line.indexOf(':');
		   assertTrue(i > 0);
		   assertFalse("transfer-encoding".equalsIgnoreCase(line.substring(0, i)));
		   line = readLine(is);
	   }
   }

   private void writeString(OutputStream os, String s) throws IOException 
   {
      LOG.info(">>" + s);
      os.write((s + "\r\n").getBytes());
   }
   
   /**
    * Lifted from Acme.Serve.Serve
    */
   private String readLine(InputStream in) throws IOException
   {
	  int maxLen = 1024;
      StringBuffer buf = new StringBuffer(Math.min(1024, maxLen));

      int c;
      boolean cr = false;
      int i = 0;
      while ((c = in.read()) != -1)
      {
         if (c == 10)
         { // LF
            if (cr)
               break;
            break;
            //throw new IOException ("LF without CR");
         }
         else if (c == 13) // CR
            cr = true;
         else
         {
            //if (cr)
            //throw new IOException ("CR without LF");
            // see http://www.w3.org/Protocols/HTTP/1.1/rfc2616bis/draft-lafon-rfc2616bis-03.html#tolerant.applications
            cr = false;
            if (i >= maxLen)
               throw new IOException("Line length exceeds " + maxLen);
            buf.append((char) c);
            i++;
         }
      }
      if (c == -1 && buf.length() == 0)
         return null;

      return buf.toString();
   }
   
   @Path("/")
   static public class SimpleResource
   {
      @HEAD
      @Path("/head")
      @Consumes("text/plain")
      public Response head()
      {
         return Response.noContent().build();
      }
      
      @PUT
      @Path("/continue")
      @Consumes("text/plain")
      public Response putContinue()
      {
         return Response.status(100).build();
      }
      
      @PUT
      @Path("/nocontent")
      @Consumes("text/plain")
      public void putNoContent(String body)
      {
         LOG.info(body);
      }
      
      @GET
      @Path("/notmodified")
      @Consumes("text/plain")
      public Response getNotModified() throws Exception
      {
         return Response.notModified().build();
      }
   }
}