package org.jboss.resteasy.mom;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Enumeration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueBrowserResource
{
   private Connection connection;
   private Queue queue;
   private MessageProcessor processor;

   public QueueBrowserResource(Connection connection, Queue queue, MessageProcessor processor)
   {
      this.connection = connection;
      this.queue = queue;
      this.processor = processor;
   }

   @GET
   @Produces("text/html")
   public String getBrowser(@QueryParam("index") @DefaultValue("0")int index, @Context UriInfo info) throws Exception
   {
      StringBuffer buffer = new StringBuffer("<html><body>");
      buffer.append("<h1>Queue Message Browser: ");
      buffer.append(queue.getQueueName()).append("</h1>");
      buffer.append("<ul>");
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      try
      {
         QueueBrowser browser = session.createBrowser(queue);
         Enumeration en = browser.getEnumeration();
         int count = 0;
         int i = 0;
         for (; en.hasMoreElements() && count < 10; i++)
         {
            Message message = (Message) en.nextElement();
            if (i >= index)
            {
               buffer.append("<li><a href=\"");
               URI uri = info.getAbsolutePathBuilder().path("messages").path(message.getJMSMessageID()).build();
               buffer.append(uri.toString()).append("\">").append(message.getJMSMessageID()).append("</a></li>");
               count++;
            }
         }


         buffer.append("</ul>");
         if (en.hasMoreElements())
         {
            buffer.append("<a href=\"").append(info.getAbsolutePath().toString());
            buffer.append("?index=").append(Integer.toString(i)).append("\">More...</a>");
         }
         buffer.append("</body></html>");
         return buffer.toString();
      }
      finally
      {
         session.close();
      }
   }

   @GET
   @Path("/messages/{id}")
   public Response get(@PathParam("id")String id) throws Exception
   {
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      try
      {
         QueueBrowser browser = session.createBrowser(queue);
         Enumeration en = browser.getEnumeration();
         Message target = null;
         while (en.hasMoreElements())
         {
            Message message = (Message) en.nextElement();
            if (message.getJMSMessageID().equals(id))
            {
               target = message;
               break;
            }
         }
         if (target == null)
         {
            return Response.status(HttpResponseCodes.SC_NOT_FOUND).build();
         }
         return processor.extractResponse(target);
      }
      finally
      {
         session.close();
      }
   }
}
