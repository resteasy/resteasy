package org.jboss.resteasy.cdi.interceptors;

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
 * Copyright Jul 23, 2012
 */
@Provider
public class BookReaderInterceptor implements ReaderInterceptor
{
   @Inject private Logger log;

   @Override
   @ReaderInterceptorBinding
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
   {
      log.info("*** Intercepting call in BookReaderInterceptor.aroundReadFrom()");
      VisitList.add(this);
      Object result = context.proceed();
      log.info("*** Back from intercepting call in BookReaderInterceptor.aroundReadFrom()");
      return result;
   }
}

