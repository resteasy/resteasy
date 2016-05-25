package org.jboss.resteasy.cdi.events;

import java.io.IOException;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 23, 2012
 */
@Provider
@ServerInterceptor
public class BookReaderInterceptor implements ReaderInterceptor
{
   @Inject @ReadIntercept Event<String> readInterceptEvent;
   @Inject private Logger log;

   @Override
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
   {
      log.info("*** Intercepting call in BookReaderInterceptor.read()");
      log.info("BookReaderInterceptor firing readInterceptEvent");
      readInterceptEvent.fire("readInterceptEvent");
      Object result = context.proceed();
      log.info("*** Back from intercepting call in BookReaderInterceptor.read()");
      return result;
   }

}

