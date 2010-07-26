package org.hornetq.rest.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.rest.queue.DestinationServiceManager;
import org.hornetq.rest.queue.QueueConsumer;
import org.hornetq.rest.util.LinkStrategy;

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

   public SubscriptionResource(ClientSessionFactory factory, String destination, String id, DestinationServiceManager serviceManager)
           throws HornetQException
   {
      super(factory, destination, id, serviceManager);
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
   protected void setMessageResponseLinks(UriInfo info, String basePath, Response.ResponseBuilder responseBuilder, String index)
   {
      setConsumeNextLink(serviceManager.getLinkStrategy(), responseBuilder, info, basePath, index);
      setSubscriptionLink(serviceManager.getLinkStrategy(), responseBuilder, info, basePath);
   }

   public static void setSubscriptionLink(LinkStrategy linkStrategy, Response.ResponseBuilder response, UriInfo info, String basePath)
   {
      UriBuilder builder = info.getBaseUriBuilder();
      builder.path(basePath);
      String uri = builder.build().toString();
      linkStrategy.setLinkHeader(response, "subscription", "subscription", uri, MediaType.APPLICATION_XML);
   }

}
