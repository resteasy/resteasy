package org.jboss.resteasy.resteasy767;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2013
 */
@Provider
@ServerInterceptor
public class TestMessageBodyWriterInterceptor implements MessageBodyWriterInterceptor
{
   public static boolean called;

   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      called = true;
      context.proceed();
   }
}