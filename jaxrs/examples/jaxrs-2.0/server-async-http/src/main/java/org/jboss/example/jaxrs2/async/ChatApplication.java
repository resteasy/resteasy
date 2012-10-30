package org.jboss.example.jaxrs2.async;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Application;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ChatApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public ChatApplication()
   {
      List<AsyncResponse> listeners = new ArrayList<AsyncResponse>();
      singletons.add(new ChatListener(listeners));
      singletons.add(new ChatSpeaker(listeners));
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return empty;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

}
