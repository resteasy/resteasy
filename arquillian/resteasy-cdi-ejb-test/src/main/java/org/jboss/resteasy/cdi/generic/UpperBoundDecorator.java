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
public abstract class UpperBoundDecorator<T extends HierarchyHolder<? extends Primate>> implements UpperBoundTypedResourceIntf<T>
{
   @Inject private Logger log;
   
   private UpperBoundTypedResource<T> resource;

   @Inject
   public UpperBoundDecorator(@Delegate UpperBoundTypedResource<T> resource)
//   public UpperBoundDecorator(@Delegate @ResourceBinding UpperBoundTypedResource<T> resource)
   {
      this.resource = resource;
      System.out.println("UpperBoundDecorator got delegate: " + resource);
   }
   
   @Override
   public Response execute()
   {
      log.info("entering UpperBoundDecorator.execute()");
      log.info("delegate: " + resource);
      log.info("type argument: " + resource.getTypeArgument());
      VisitList.add(VisitList.UPPER_BOUND_DECORATOR_ENTER);
      Response response = resource.execute();
      VisitList.add(VisitList.UPPER_BOUND_DECORATOR_LEAVE);
      log.info("leaving UpperBoundDecorator.execute()");
      return response;
   }
}
