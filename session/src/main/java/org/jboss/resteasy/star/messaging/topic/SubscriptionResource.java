package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.LinkHeaderSupport;
import org.jboss.resteasy.star.messaging.queue.ConsumerFactory;
import org.jboss.resteasy.star.messaging.queue.QueueConsumer;

import javax.ws.rs.HEAD;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SubscriptionResource extends QueueConsumer implements Subscription
{
   boolean durable;

   public SubscriptionResource(ClientSessionFactory factory, String destination, String id)
           throws HornetQException
   {
      super(factory, destination, id);
   }

   public boolean isDurable()
   {
      return durable;
   }

   public void setDurable(boolean durable)
   {
      this.durable = durable;
   }

   @Override
   protected void setMessageResponseLinks(UriInfo info, String basePath, Response.ResponseBuilder responseBuilder)
   {
      setConsumeNextLink(responseBuilder, info, basePath);
      setSubscriptionLink(responseBuilder, info, basePath);
   }

   public static void setSubscriptionLink(Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      String uri = builder.build().toString();
      Link link = new Link("subscription", "subscription", uri, MediaType.APPLICATION_XML, null);
      LinkHeaderSupport.setLinkHeader(response, link);
   }

   public static ConsumerFactory getFactory()
   {
      return new ConsumerFactory()
      {
         @Override
         public QueueConsumer createConsumer(String id, ClientSessionFactory factory, String destination) throws HornetQException
         {
            return new SubscriptionResource(factory, destination, id);
         }
      };
   }

}
