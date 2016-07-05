package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Provider
public class BookReaderInterceptor implements ReaderInterceptor
{
   @Inject private Logger log;

   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
   {
      log.info("entering BookReaderInterceptor.aroundReadFrom()");
      VisitList.add(VisitList.READER_INTERCEPTOR_ENTER);
      Object result = context.proceed();
      VisitList.add(VisitList.READER_INTERCEPTOR_LEAVE);
      log.info("leaving BookReaderInterceptor.aroundReadFrom()");
      return result;
   }
}

