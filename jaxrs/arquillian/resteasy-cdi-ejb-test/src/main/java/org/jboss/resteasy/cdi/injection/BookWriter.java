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
package org.jboss.resteasy.cdi.injection;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Provider
@Produces(Constants.MEDIA_TYPE_TEST_XML)
@ApplicationScoped
public class BookWriter implements MessageBodyWriter<Book>
{
   static private MessageBodyWriter<Book> delegate;

   @Inject private DependentScoped dependent;
   @Inject private StatefulEJB stateful;
   
   static
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyWriter(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
   }
   
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Book.class.equals(type);
   }

   public long getSize(Book t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(Book t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }
   
   public DependentScoped getDependent()
   {
      return dependent;
   }
   

   public StatefulEJB getStateful()
   {
      return stateful;
   }
}

