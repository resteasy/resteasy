package org.jboss.resteasy.test.providers.jaxb.regression;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JAXB Had a concurrent problem and was not unmarshalling a Map property all the time
 */
public class WebTest
{

   private static Dispatcher dispatcher;
   private static int iterator = 500;
   private static AtomicInteger counter = new AtomicInteger();
   private static CountDownLatch latch;
   private static JAXBContext ctx;
   private static String itemString;

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setAsyncJobServiceEnabled(true);
      EmbeddedContainer.start(deployment);

      System.out.println("hello");
      dispatcher = deployment.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(AsyncService.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private ApacheHttpClient4Executor createClient()
   {
      HttpParams params = new BasicHttpParams();
      ConnManagerParams.setMaxTotalConnections(params, 500);
      ConnManagerParams.setTimeout(params, 5000);

      // Create and initialize scheme registry
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(
              new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

      // Create an HttpClient with the ThreadSafeClientConnManager.
      // This connection manager must be used if more than one thread will
      // be using the HttpClient.
      ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
      HttpClient httpClient = new DefaultHttpClient(cm, params);

      final ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
      return executor;

   }

   @Test
   public void testMe() throws Exception
   {
      latch = new CountDownLatch(iterator);
      ctx = JAXBContext.newInstance(Item.class);
      counter.set(0);

      // TODO Auto-generated method stub

      ClientExecutor executor = createClient();

      itemString = setString();

      System.out.println(itemString);

      for (int i = 0; i < iterator; i++)
      {

         String u = TestPortProvider.generateURL("/mpac/add?oneway=true");
         //String u = TestPortProvider.generateURL("/mpac/add");
         //System.out.println(u);


         //System.out.println(sbuffer.toString());

         /*
         URL url = new URL(u);//?oneway=true"));
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("POST");
         connection.setRequestProperty("Content-Type", "application/xml");
         connection.setDoOutput(true);
         connection.setInstanceFollowRedirects(false);
         OutputStream os = connection.getOutputStream();
         os.write(sbuffer.toString().getBytes());
         os.flush();

         System.out.println("test " + i);
         Assert.assertEquals(204, connection.getResponseCode());

         //System.out.println("Iterator " + i + " " + connection.getResponseCode());
         connection.disconnect();
         */

         ClientRequest request = new ClientRequest(u, executor);
         request.body("application/xml", itemString);
         ClientResponse response = request.post();
         response.releaseConnection();
         Assert.assertEquals(202, response.getStatus());


      }
      latch.await(10, TimeUnit.SECONDS);
      Assert.assertEquals(iterator, counter.get());
   }

   private String setString()
   {
      StringBuffer sbuffer = new StringBuffer();
      sbuffer.append("<item>");
      sbuffer.append("<price>1000</price>");
      sbuffer.append("<description>Allah Hafiz</description>");
      sbuffer.append("<requestID>");
      sbuffer.append("i");
      sbuffer.append("</requestID>");

      sbuffer.append("<dummy1>DUMMY1</dummy1>");
      sbuffer.append("<dummy2>DUMMY2</dummy2>");
      sbuffer.append("<dummy3>DUMMY3</dummy3>");
      sbuffer.append("<dummy4>DUMMY4</dummy4>");
      sbuffer.append("<dummy5>DUMMY5</dummy5>");
      sbuffer.append("<dummy6>DUMMY6</dummy6>");
      sbuffer.append("<dummy7>DUMMY7</dummy7>");
      sbuffer.append("<dummy8>DUMMY8</dummy8>");

      sbuffer.append("<harness>");
      sbuffer.append("<entry>");
      sbuffer.append("<key>P_REGIONCD</key>");
      sbuffer.append("<value>325</value>");
      sbuffer.append("</entry>");
      sbuffer.append("<entry>");
      sbuffer.append("<key>P_COUNTYMUN</key>");
      sbuffer.append("<value>447</value>");
      sbuffer.append("</entry>");
      sbuffer.append("<entry>f");
      sbuffer.append("<key>p_SrcView</key>");
      sbuffer.append("<value>C</value>");
      sbuffer.append("</entry>");
      sbuffer.append("</harness>");

      sbuffer.append("</item>");
      return sbuffer.toString();

   }

   @Test
   public void compare() throws Exception
   {
      itemString = setString();
      ctx = JAXBContext.newInstance(Item.class);

      counter.set(0);

      Thread[] threads = new Thread[iterator];
      for (int i = 0; i < iterator; i++)
      {
         Thread thread = new Thread()
         {
            @Override
            public void run()
            {
               byte[] bytes = itemString.getBytes();
               ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
               Item item = null;
               try
               {
                  item = (Item) ctx.createUnmarshaller().unmarshal(bais);
               }
               catch (JAXBException e)
               {
                  throw new RuntimeException(e);
               }
               item.toString();
               counter.incrementAndGet();

            }
         };
         threads[i] = thread;
      }
      for (int i = 0; i < iterator; i++) threads[i].start();
      for (int i = 0; i < iterator; i++) threads[i].join();
      Assert.assertEquals(iterator, counter.get());


   }

   @Path("/mpac")
   public static class AsyncService
   {


      @GET()
      @Produces("text/plain")
      public String sayHello()
      {
         return "Hello World!";
      }


      public void addSchedule(Item item)
      {
         try
         {
            Assert.assertNotNull(item);
            item.toString();
            counter.incrementAndGet();
         }
         finally
         {
            latch.countDown();
         }
      }

      @POST()
      @Path("/add")
      @Consumes("application/xml")
      public void addSchedule(byte[] bytes) throws Exception
      {
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         Item item = (Item) ctx.createUnmarshaller().unmarshal(bais);
         try
         {
            addSchedule(item);
         }
         catch (Exception ex)
         {
            String str = new String(bytes);
            String msg = "Failed ";
            if (!str.equals(itemString)) msg += " with " + str;
            throw new Exception(msg);
         }
      }

   }
}


