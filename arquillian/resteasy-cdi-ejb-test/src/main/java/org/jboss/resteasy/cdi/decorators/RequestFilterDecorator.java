package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class RequestFilterDecorator implements ContainerRequestFilter
{
   @Inject private Logger log;
   @Inject private @Delegate TestRequestFilter filter;

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      log.info("entering RequestFilterDecorator.filter()");
      VisitList.add(VisitList.REQUEST_FILTER_DECORATOR_ENTER);
      filter.filter(requestContext);
      VisitList.add(VisitList.REQUEST_FILTER_DECORATOR_LEAVE);
      log.info("leaving RequestFilterDecorator.filter()");
   }
}
