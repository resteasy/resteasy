package org.jboss.resteasy.cdi.decorators;

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
 * Copyright Nov 13, 2012
 */
@Provider
public class BookWriterInterceptor implements WriterInterceptor
{
   @Inject private Logger log;

   @Override
   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
   {
      log.info("entering BookWriterInterceptor.aroundWriteTo()");
      VisitList.add(VisitList.WRITER_INTERCEPTOR_ENTER);
      context.proceed();
      VisitList.add(VisitList.WRITER_INTERCEPTOR_LEAVE);
      log.info("leaving BookWriterInterceptor.aroundWriteTo()");
   }
}

