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
 */
@Decorator
public abstract class ConcreteDecorator implements ConcreteResourceIntf
{
   @Inject private Logger log;
   
   private ConcreteResourceIntf resource;

   @Inject
   public ConcreteDecorator(@Delegate ConcreteResourceIntf resource)
   {
      this.resource = resource;
      System.out.println("ConcreteDecorator got delegate: " + resource);
   }
   
   @Override
   public Response execute()
   {
      log.info("entering ConcreteDecorator.execute()");
      VisitList.add(VisitList.CONCRETE_DECORATOR_ENTER);
      Response response = resource.testGenerics();
      VisitList.add(VisitList.CONCRETE_DECORATOR_LEAVE);
      log.info("leaving ConcreteDecorator.execute()");
      return response;
   }
   
   @Override
   abstract public Response testDecorators();
}
