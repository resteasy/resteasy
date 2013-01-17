package org.jboss.resteasy.cdi.decorators;

import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class BookWriterInterceptorDecorator implements WriterInterceptor
{
   @Inject private Logger log;
   @Inject private @Delegate BookWriterInterceptor interceptor;

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws java.io.IOException, javax.ws.rs.WebApplicationException
   {
      log.info("entering BookWriterInterceptorDecorator.aroundWriteTo()");
      VisitList.add(VisitList.WRITER_INTERCEPTOR_DECORATOR_ENTER);
      interceptor.aroundWriteTo(context);
      VisitList.add(VisitList.WRITER_INTERCEPTOR_DECORATOR_LEAVE);
      log.info("leaving BookWriterInterceptorDecorator.aroundWriteTo()");
   }
}
