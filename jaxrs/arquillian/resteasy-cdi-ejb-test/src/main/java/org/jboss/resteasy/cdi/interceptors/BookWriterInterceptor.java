package org.jboss.resteasy.cdi.interceptors;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 23, 2012
 */
@Provider
public class BookWriterInterceptor implements WriterInterceptor
{
   @Inject private Logger log;

   @Override
   @WriterInterceptorBinding
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      log.info("*** Intercepting call in BookWriterInterceptor.aroundWriteTo()");
      VisitList.add(this);
      context.proceed();
      log.info("*** Back from intercepting call in BookWriterInterceptor.aroundWriteTo()");
   }

}

