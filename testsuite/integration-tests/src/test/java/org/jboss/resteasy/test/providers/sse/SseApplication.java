package org.jboss.resteasy.test.providers.sse;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

@Provider
public class SseApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();

   public Set<Object> getSingletons()
   {
      if (singletons.isEmpty())
      {
    	 SseResource sseResource = new SseResource();
         singletons.add(sseResource);
         singletons.add(new AnotherSseResource(sseResource));
         singletons.add(new EscapingSseResource());
      }
      return singletons;
   }

}
