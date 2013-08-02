package org.jboss.resteasy.cdi.generic;

import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 15, 2012
 * 
 * @see https://community.jboss.org/message/784561#784561
 */
@Decorator
public abstract class LowerBoundDecorator<T extends HierarchyHolder<? super Primate>> implements LowerBoundTypedResourceIntf<T>
{
   @Inject private Logger log;
   
   private LowerBoundTypedResource<T> resource;

   @Inject
   public LowerBoundDecorator(@Delegate LowerBoundTypedResource<T> resource)
//   public LowerBoundDecorator(@Delegate @ResourceBinding LowerBoundTypedResource<T> resource)
   {
      this.resource = resource;
      System.out.println("LowerBoundDecorator got delegate: " + resource);
   }
   
   @Override
   public Response execute()
   {
      log.info("entering LowerBoundDecorator.execute()");
      log.info("delegate: " + resource);
      log.info("type argument: " + resource.getTypeArgument());
      VisitList.add(VisitList.LOWER_BOUND_DECORATOR_ENTER);
      Response response = resource.execute();
      VisitList.add(VisitList.LOWER_BOUND_DECORATOR_LEAVE);
      log.info("leaving LowerBoundDecorator.execute()");
      return response;
   }
}
