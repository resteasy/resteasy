package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class BookReaderInterceptorDecorator implements ReaderInterceptor
{
   @Inject private Logger log;
   @Inject private @Delegate BookReaderInterceptor interceptor;

   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
   {
      log.info("entering BookReaderInterceptorDecorator.aroundReadFrom()");
      VisitList.add(VisitList.READER_INTERCEPTOR_DECORATOR_ENTER);
      Object o = interceptor.aroundReadFrom(context);
      VisitList.add(VisitList.READER_INTERCEPTOR_DECORATOR_LEAVE);
      log.info("leaving BookReaderInterceptorDecorator.aroundReadFrom()");
      return o;
   }
}
