/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.resteasy.cdi.events;

import java.io.IOException;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 23, 2012
 */
@Provider
@ServerInterceptor
public class BookReaderInterceptor implements MessageBodyReaderInterceptor
{
   @Inject @ReadIntercept Event<String> readInterceptEvent;
   @Inject private Logger log;

   @Override
   public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException
   {
      log.info("*** Intercepting call in BookReaderInterceptor.read()");
      log.info("BookReaderInterceptor firing readInterceptEvent");
      readInterceptEvent.fire("readInterceptEvent");
      Object result = context.proceed();
      log.info("*** Back from intercepting call in BookReaderInterceptor.read()");
      return result;
   }

}

