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
package org.jboss.resteasy.ejb;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.persistence.Entity;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Stateful
@LocalBean 
@Provider
@Produces(Constants.MEDIA_TYPE_TEST_XML)
public class EJBBookWriterImpl implements MessageBodyWriter<Book>//EJBBookWriter<Book>
{
   @SuppressWarnings("rawtypes")
   static private MessageBodyWriter delegate;
   static private int uses;
   
   @Entity
   @XmlRootElement(name = "nonbook")
   @XmlAccessorType(XmlAccessType.FIELD)
   public class NonBook
   {
   }
   
   /*
    * It seems that EJBBookWriterImpl is treated somewhat differently than EJBBookReaderImpl, perhaps
    * because EJBBookWriterImpl has a no-interface view.  In any case, EJBBookReaderImpl is able to
    * get an instance of ResteasyProviderFactory in a static block, but EJBBookWriterImpl isn't.
    */
   static void getDelegate()
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyWriter(NonBook.class, null, null, MediaType.APPLICATION_XML_TYPE);
      System.out.println("writer delegate: " + delegate);  // Should be JAXBXmlRootElementProvider.
   }
   
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Book.class.equals(type);
   }

   public long getSize(Book t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void writeTo(Book t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      if (delegate == null)
      {
         getDelegate();
      }
      delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      uses++;
   }
   
   public int getUses()
   {
      return uses;
   }
   
   public void reset()
   {
      uses = 0;
   }
}

