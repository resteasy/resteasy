package org.jboss.resteasy.cdi.events.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 7, 2012
 */
@Stateless
public class EventSourceImpl implements EventSource
{  
   static private Map<Integer, Book> collection = new HashMap<Integer, Book>();
   static private AtomicInteger counter = new AtomicInteger();

   @Inject @Process Event<String> processEvent;
   @Inject @Read(context="resource")  @Process Event<String> readProcessEvent;
   @Inject @Write(context="resource") @Process Event<String> writeProcessEvent;
   @Inject EventObserver eventObserver;
   @Inject private Logger log;
   
   public boolean test()
   {
      log.info("entering EventSourceImpl.test()");
      ArrayList<Object> eventList = eventObserver.getEventList();
      for (int i = 0; i < eventList.size(); i++)
      {
         log.info(eventList.get(i).toString());
      }
      log.info("leaving EventSourceImpl.test()");
      return true;
   }
   
   public int createBook(Book book)
   {
      log.info("entering EventSourceImpl.createBook()");
      log.info("EventSourceImpl firing processEvent");
      processEvent.fire("processEvent");
      int id = counter.getAndIncrement();
      book.setId(id);
      collection.put(id, book);
      log.info("stored: " + id + "->" + book);
      log.info("EventSourceImpl firing readProcessEvent");
      readProcessEvent.fire("readProcessEvent");
      log.info("leaving EventSourceImpl.createBook()");
      return id;
   }

   public Book lookupBookById(@PathParam("id") int id)
   {
      log.info("entering EventSourceImpl.lookupBookById(" + id + ")");
      log.info("books: " + collection);
      log.info("EventSourceImpl firing processEvent");
      processEvent.fire("processEvent");
      Book book = collection.get(id);
      log.info("EventSourceImpl firing writeProcessEvent");
      writeProcessEvent.fire("writeProcessEvent");
      if (book == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      log.info("leaving EventSourceImpl.lookupBookById(" + id + ")");
      return book;
   }
}
