package org.resteasy.mom;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Listener implements MessageListener
{
   protected Destination destination;
   protected Connection connection;
   protected MessageConsumer consumer;
   protected Session session;
   protected HttpClient httpClient = new HttpClient();
   protected String callback;
   protected MessageProcessor processor;

   public Listener(Destination destination, Connection connection, String callback, MessageProcessor processor) throws Exception
   {
      this.destination = destination;
      this.connection = connection;
      this.callback = callback;
      this.session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
      this.processor = processor;
   }

   protected boolean isDead(int status)
   {
      switch (status)
      {
         case HttpResponseCodes.SC_REQUEST_TIMEOUT:
         case HttpResponseCodes.SC_CONFLICT:
         case HttpResponseCodes.SC_REQUEST_ENTITY_TOO_LARGE:
         case HttpResponseCodes.SC_SERVICE_UNAVAILABLE:
         case HttpResponseCodes.SC_GATEWAY_TIMEOUT:
            return false;

      }
      return true;
   }

   public void onMessage(Message message)
   {
      try
      {
         System.out.println("****** ON MESSAGE");
         byte[] body = processor.extractBody(message);
         System.out.println("LISTENER RECEIVED body length: " + body.length);
         Map<String, String> headers = processor.extractHeaders(message);

         PostMethod method = new PostMethod(callback);
         for (String key : headers.keySet())
         {
            method.setRequestHeader(key, headers.get(key));
         }
         method.setRequestEntity(new ByteArrayRequestEntity(body));
         HttpMethodParams params = new HttpMethodParams();
         params.setSoTimeout(1000);
         method.setParams(params);
         try
         {
            int status = httpClient.executeMethod(method);
            if (status == HttpResponseCodes.SC_OK)
            {
               System.out.println("message sent to listener: " + callback);
               return;
            }
         }
         catch (Exception e)
         {
         }
         System.out.println("Failed sending to listener, deadlettering");
         processor.deadletter(message);
      }
      catch (Throwable throwable)
      {
         throwable.printStackTrace();
      }
      finally
      {
         try
         {
            System.out.println("ACKNOWLEDGING!!!!!");
            message.acknowledge();
         }
         catch (JMSException ignored)
         {
         }
      }
   }

   public synchronized void close()
   {
      try
      {
         if (consumer != null) consumer.close();
      }
      catch (JMSException ignored) {}
      consumer = null;

      try
      {
         if (session != null) session.close();
      }
      catch (JMSException ignore) {}
      session = null;
   }
}