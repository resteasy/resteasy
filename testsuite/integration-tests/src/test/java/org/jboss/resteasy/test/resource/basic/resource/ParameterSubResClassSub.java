package org.jboss.resteasy.test.resource.basic.resource;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
@ApplicationScoped
public class ParameterSubResClassSub
{
   AtomicInteger resourceCounter = new AtomicInteger();
   @Inject
   ApplicationScopeObject appScope;

   @Inject
   RequestScopedObject requestScope;
   
   @GET
   @Produces("text/plain")
   public String get()
   {
      return "resourceCounter:" + resourceCounter.incrementAndGet() + ",appscope:" + appScope.getCount() + ",requestScope:" + requestScope.getCount();
   }
}
