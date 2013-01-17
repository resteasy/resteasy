package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class ResponseFilterDecorator implements ContainerResponseFilter
{
   @Inject private Logger log;
   @Inject private @Delegate TestResponseFilter filter;

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
   {
      log.info("entering RequestFilterDecorator.filter()");
      VisitList.add(VisitList.RESPONSE_FILTER_DECORATOR_ENTER);
      filter.filter(requestContext, responseContext);
      VisitList.add(VisitList.RESPONSE_FILTER_DECORATOR_LEAVE);
      log.info("leaving RequestFilterDecorator.filter()");
   }
}
