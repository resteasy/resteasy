package org.jboss.resteasy.star.messaging.integration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmbeddedRestHornetQJMS extends EmbeddedRestHornetQ
{
   @Override
   protected void initEmbeddedHornetQ()
   {
      embeddedHornetQ = new EmbeddedHornetQJMS();
   }

   public ComponentRegistry getRegistry()
   {
      return ((EmbeddedHornetQJMS) embeddedHornetQ).getRegistry();
   }


}
