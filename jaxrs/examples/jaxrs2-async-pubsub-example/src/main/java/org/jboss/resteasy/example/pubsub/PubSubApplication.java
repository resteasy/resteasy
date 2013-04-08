package org.jboss.resteasy.example.pubsub;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PubSubApplication extends Application
{
   protected Set<Object> singletons = new HashSet<Object>();

   public PubSubApplication()
   {
      singletons.add(new SubscriptionResource());
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
