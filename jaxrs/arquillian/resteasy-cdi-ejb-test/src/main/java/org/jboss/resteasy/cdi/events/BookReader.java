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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
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
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class BookReader implements MessageBodyReader<Book>
{
   static private MessageBodyReader<Book> delegate;

   @Inject @Read(context="reader") Event<String> readEvent;
   @Inject private Logger log;
   
   private ArrayList<Object> eventList = new ArrayList<Object>();
   
   static
   {
      System.out.println("In BookReader static {}");
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyReader(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
      System.out.println("In BookReader static {}");
   }
   
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      System.out.println("entering BookReader.isReadable()");
      boolean b = Book.class.equals(type);
      System.out.println("leaving BookReader.isReadable()");
      return b;
   }

   public Book readFrom(Class<Book> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering BookReader.readFrom()");
      Book book = Book.class.cast(delegate.readFrom(Book.class, genericType, annotations, mediaType, httpHeaders, entityStream));
      log.info("BookReader firing readEvent");
      readEvent.fire("readEvent");
      log.info("BookReader.readFrom() read " + book);
      log.info("leaving BookReader.readFrom()");
      return book;
   }
   
   public void readIntercept(@Observes @ReadIntercept String event)
   {
      eventList.add(event);
      log.info("BookReader.readIntercept() got " + event);
   }
   
   public void read(@Observes @Read(context="reader") String event)
   {
      eventList.add(event);
      log.info("BookReader.read() got " + event);
   }
   
   public void writeIntercept(@Observes @WriteIntercept String event)
   {
      eventList.add(event);
      log.info("BookReader.writeIntercept() got " + event);
   }
   
   public void write(@Observes @Write(context="writer") String event)
   {
      eventList.add(event);
      log.info("BookReader.write() got " + event);
   }
   
   public void process(@Observes @Process String event)
   {
      eventList.add(event);
      log.info("BookReader.process() got " + event);
   }
   
   public void processRead(@Observes @Process @Read(context="resource") String event)
   {
      eventList.add(event);
      log.info("BookReader.processRead() got " + event);
   }
   
   public void processWrite(@Observes @Process @Write(context="resource") String event)
   {
      eventList.add(event);
      log.info("BookReader.processWrite() got " + event);
   }
   
   public void unused(@Observes @Read(context="unused") @Write(context="unused") Unused event)
   {
      eventList.add(event);
      log.info("BookReader.unused() got " + event);
      throw new RuntimeException("BookReader.unused() got " + event);
   }
   
   public ArrayList<Object> getEventList()
   {
      return new ArrayList<Object>(eventList);
   }
}

