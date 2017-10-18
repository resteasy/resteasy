package org.jboss.resteasy.test.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpTestCaseDetails Verify request is sent in chunked format
 * @tpSince RESTEasy 3.1.4
 */
public class ChunkedTransferEncodingUnitTest
{
   private static Thread t;
   private static ServerSocket ss;
   private static Socket s;
   private final static Logger logger = Logger.getLogger(ChunkedTransferEncodingUnitTest.class);
   
   private static String RESPONSE_200 =
         "HTTP/1.1 200 OK\r\n" +
         "Content-Type: text/plain;charset=UTF-8\r\n" +
         "Content-Length: 2\r\n" +
         "\r\n" +
         "ok";
   
   private static String RESPONSE_400 =
         "HTTP/1.1 400 OK\r\n" +
         "Content-Type: text/plain;charset=UTF-8\r\n" +
         "Content-Length: 6\r\n" +
         "\r\n" +
         "not ok";
              
   static final String testFilePath;

   static {
       testFilePath = TestUtil.getResourcePath(ChunkedTransferEncodingUnitTest.class, "ChunkedTransferEncodingUnitTestFile");
   }
   
   //////////////////////////////////////////////////////////////////////////////
   
   @Before
   public void before() throws Exception
   {
      final int[] chars = new int[1024];
      
      t = new Thread() {
         public void run() {
            try {
               ss = new ServerSocket(8081);
               s = ss.accept();
               InputStream is = s.getInputStream();
               int j = 0;
               while (!endOfHeaders(chars, j)) {
                  chars[j++] = is.read();
               }
               String headers = new String(chars, 0, j);
               int length = getLength(chars, j, is);
               for (int k = 0; k < length; k++) {
                  chars[k] = is.read();
               }
               String entity = new String(chars, 0, length);
               OutputStream os = s.getOutputStream();
               if (headers.contains("Transfer-Encoding: chunked") && entity.contains("file entity")) {
                  os.write(RESPONSE_200.getBytes());
               }
               else {
                  os.write(RESPONSE_400.getBytes());
               }
               return;
            } catch (IOException e) {
               logger.error(e.getMessage(), e);
            }
         }
      };
      t.setDaemon(true);
      t.start();
   }

   private static boolean endOfHeaders(int[] chars, int length) {
      if (length < 4) {
         return false;
      }
      return chars[length - 4] == '\r' && chars[length - 3] == '\n' && chars[length - 2] == '\r' && chars[length - 1] == '\n';
   }
   
   private static int getLength(int[] chars, int start, InputStream is) throws IOException {
      int i = start;
      while (true) {
         chars[i] = is.read();
         while (chars[i++] != '\r') {
            chars[i] = is.read();
         }
         chars[i] = is.read();
         if (chars[i++] == '\n') {
            String s = new String(chars, start, i - start - 2);
            return Integer.valueOf(s, 16);
         }
      }
   }
   
   @After
   public void after() throws Exception
   {
      if (s != null) {
         s.close();
      }
      if (ss != null) {
         ss.close();
      }
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testChunkedTarget() throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/test");
      target.setChunked(true);
      ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
      File file = new File(testFilePath);
      Response response = request.post(Entity.entity(file, "text/plain"));
      String header = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", header);
      response.close();
      client.close();
   }
   
   @Test
   public void testChunkedRequest() throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/test");
      ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
      request.setChunked(true);
      File file = new File(testFilePath);
      Response response = request.post(Entity.entity(file, "text/plain"));
      String header = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", header);
      response.close();
      client.close();
   }
}
