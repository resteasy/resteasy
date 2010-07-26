import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcknowledgeConsume
{
   private static String session;
   private static boolean shutdown = false;
   private static Object shutdownLock = new Object();

   public static void main(String[] args) throws Exception
   {
      URL url = new URL("http://localhost:9095/queues/jms.queue.orders");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("HEAD");
      if (conn.getResponseCode() != 200)
      {
         throw new Exception("Failed to get root URL");
      }
      String consumers = conn.getHeaderField("msg-pull-consumers");
      conn.disconnect();
      url = new URL(consumers);
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      writeFormParams(conn, "autoAck=false");
      if (conn.getResponseCode() != 201)
      {
         throw new Exception("Failed to create session");
      }
      String acknowledgeNext = conn.getHeaderField("msg-acknowledge-next");
      conn.disconnect();

      Runtime.getRuntime().addShutdownHook(new Thread()
      {
         public void run()
         {
            synchronized (shutdownLock)
            {
               shutdown = true;
               if (session != null)
               {
                  System.out.println("Deleting Hornetq Session...");
                  try
                  {
                     URL url = new URL(session);
                     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                     conn.setRequestMethod("DELETE");
                     if (conn.getResponseCode() != 204)
                     {
                        System.err.println("Failed to delete a message");
                     }
                  }
                  catch (IOException e)
                  {
                     throw new RuntimeException(e);
                  }

               }
            }
         }
      }
      );

      while (true)
      {
         synchronized (shutdownLock)
         {
            if (shutdown) break;
            acknowledgeNext = receiveAndAcknowledge(acknowledgeNext);
         }
      }

   }

   private static String receiveAndAcknowledge(String acknowledgeNext)
           throws Exception
   {
      URL url;
      HttpURLConnection conn;
      url = new URL(acknowledgeNext);
      conn = (HttpURLConnection) url.openConnection();

      String acknowledgement;
      try
      {
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Accept-Wait", "2");
         conn.setInstanceFollowRedirects(false);

         System.out.println("Waiting for receive...");
         int responseCode = conn.getResponseCode();
         if (responseCode == 503)
         {
            System.out.println("Timeout receiving, retry...");
            session = conn.getHeaderField("msg-session");
            return conn.getHeaderField("msg-acknowledge-next");
         }
         else if (responseCode != 200)
         {
            throw new Exception("Failed to consume a message" + responseCode);
         }

         String xml = readText(conn);
         System.out.println("Received message");
         System.out.println("----------------");
         System.out.println(xml);
         acknowledgement = conn.getHeaderField("msg-acknowledgement");
         session = conn.getHeaderField("msg-session");
      }
      finally
      {
         conn.disconnect();
      }

      url = new URL(acknowledgement);
      conn = (HttpURLConnection) url.openConnection();
      try
      {
         conn.setRequestMethod("POST");
         conn.setInstanceFollowRedirects(false);
         writeFormParams(conn, "acknowledge=true");

         int responseCode = conn.getResponseCode();
         if (responseCode != 204)
         {
            throw new Exception("Failed to acknowledge a message" + responseCode);
         }
         return conn.getHeaderField("msg-acknowledge-next");
      }
      finally
      {
         conn.disconnect();
      }
   }

   private static String readText(HttpURLConnection conn)
           throws IOException
   {
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      String xml = "";
      while ((line = rd.readLine()) != null)
      {
         xml += line + "\n";

      }
      return xml;
   }

   private static void writeFormParams(HttpURLConnection conn, String formParams)
           throws IOException
   {
      conn.setDoOutput(true);
      OutputStream os = conn.getOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter(os);
      writer.write(formParams);
      writer.flush();
   }
}
