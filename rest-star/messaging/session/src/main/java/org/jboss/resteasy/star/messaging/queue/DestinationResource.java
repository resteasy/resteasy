package org.jboss.resteasy.star.messaging.queue;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DestinationResource
{
   protected String destination;
   protected PostMessage sender;
   protected DestinationServiceManager serviceManager;

   public DestinationServiceManager getServiceManager()
   {
      return serviceManager;
   }

   public void setServiceManager(DestinationServiceManager serviceManager)
   {
      this.serviceManager = serviceManager;
   }

   public PostMessage getSender()
   {
      return sender;
   }

   public void setSender(PostMessage sender)
   {
      this.sender = sender;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }
}
