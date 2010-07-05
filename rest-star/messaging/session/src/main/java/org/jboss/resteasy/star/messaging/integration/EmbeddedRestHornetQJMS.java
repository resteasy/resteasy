package org.jboss.resteasy.star.messaging.integration;

import org.jboss.resteasy.star.messaging.BindingRegistry;

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

   public BindingRegistry getRegistry()
   {
      return ((EmbeddedHornetQJMS) embeddedHornetQ).getRegistry();
   }


}
