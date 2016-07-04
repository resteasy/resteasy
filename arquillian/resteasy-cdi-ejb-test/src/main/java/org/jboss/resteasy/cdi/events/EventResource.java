package org.jboss.resteasy.cdi.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.cdi.util.Constants;


/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 7, 2012
 */
@Path("/")
@RequestScoped
public class EventResource
{  
   static private Map<Integer, Book> collection = new HashMap<Integer, Book>();
   static private AtomicInteger counter = new AtomicInteger();

   @Inject @Process Event<String> processEvent;
   @Inject @Read(context="resource")  @Process Event<String> readProcessEvent;
   @Inject @Write(context="resource") @Process Event<String> writeProcessEvent;
   @Inject BookReader bookReader;
   @Inject private Logger log;
   
   @POST
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   public Response test()
   {
      log.info("entering EventResource.test()");
      log.info("event list:");
      ArrayList<Object> eventList = bookReader.getEventList();
      for (int i = 0; i < eventList.size(); i++)
      {
         log.info(eventList.get(i).toString());
      }
      boolean status = true;
      if (!(eventList.size() == 20))
      {
         status = false;
         log.info("should have 20 events, not " + eventList.size());
      }
      if (!"readInterceptEvent".equals(eventList.get(0)))   // BookReader.process() or BookReader.readIntercept()
      {
         status = false;
         log.info("missing readInterceptEvent");
      }
      if (!"readInterceptEvent".equals(eventList.get(1)))   // BookReader.process() or BookReader.readIntercept()
      {
         status = false;
         log.info("missing readInterceptEvent");
      }
      if (!"readEvent".equals(eventList.get(2)))            // BookReader.process() or BookReader.read()
      {
         status = false;
         log.info("missing readEvent");
      }
      if (!"readEvent".equals(eventList.get(3)))            // BookReader.process() or BookReader.read()
      {
         status = false;
         log.info("missing readEvent");
      }
      if (!"processEvent".equals(eventList.get(4)))         // BookReader.process()
      {
         status = false;
         log.info("missing processEvent");
      }
      if (!"readProcessEvent".equals(eventList.get(5)))     // BookReader.process() or BookReader.readProcess()
      {
         status = false;
         log.info("missing readProcessEvent");
      }
      if (!"readProcessEvent".equals(eventList.get(6)))     // BookReader.process() or BookReader.readProcess()
      {
         status = false;
         log.info("missing readProcessEvent");
      }
      if (!"writeInterceptEvent".equals(eventList.get(7)))  // BookReader.process() or BookReader.writeIntercept()
      {
         status = false;
         log.info("missing writeInterceptEvent");
      }
      if (!"writeInterceptEvent".equals(eventList.get(8)))  // BookReader.process() or BookReader.writeIntercept()
      {
         status = false;
         log.info("missing writeInterceptEvent");
      }
      if (!"processEvent".equals(eventList.get(9)))         // BookReader.process()
      {
         status = false;
         log.info("missing processEvent");
      }
      if (!"writeProcessEvent".equals(eventList.get(10)))   // BookReader.process() or BookReader.writeProcess()
      {
         status = false;
         log.info("missing writeProcessEvent");
      }
      if (!"writeProcessEvent".equals(eventList.get(11)))   // BookReader.process() or BookReader.writeProcess()
      {
         status = false;
         log.info("missing writeProcessEvent");
      }
      if (!"writeInterceptEvent".equals(eventList.get(12))) // BookReader.process() or BookReader.writeIntercept()
      {
         status = false;
         log.info("missing writeInterceptEvent");
      }
      if (!"writeInterceptEvent".equals(eventList.get(13))) // BookReader.process() or BookReader.writeIntercept()
      {
         status = false;
         log.info("missing writeInterceptEvent");
      }
      if (!"writeEvent".equals(eventList.get(14)))          // BookReader.process() or BookReader.write()
      {
         status = false;
         log.info("missing writeEvent");
      }
      if (!"writeEvent".equals(eventList.get(15)))          // BookReader.process() or BookReader.write()
      {
         status = false;
         log.info("missing writeEvent");
      }
      if (!"readInterceptEvent".equals(eventList.get(16)))  // BookReader.process() or BookReader.readIntercept()
      {
         status = false;
         log.info("missing readInterceptEvent");
      }
      if (!"readInterceptEvent".equals(eventList.get(17)))  // BookReader.process() or BookReader.readIntercept()
      {
         status = false;
         log.info("missing readInterceptEvent");
      }
      if (!"readEvent".equals(eventList.get(18)))           // BookReader.process() or BookReader.read()
      {
         status = false;
         log.info("missing readEvent");
      }
      if (!"readEvent".equals(eventList.get(19)))           // BookReader.process() or BookReader.read()
      {
         status = false;
         log.info("missing readEvent");
      }
      
      log.info("leaving EventResource.test()");
      return status ? Response.ok().build() : Response.serverError().build();
   }
   
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   @Produces(MediaType.TEXT_PLAIN)
   public Response createBook(Book book)
   {
      log.info("entering EventResource.createBook()");
      log.info("EventResource firing processEvent");
      processEvent.fire("processEvent");
      int id = counter.getAndIncrement();
      book.setId(id);
      collection.put(id, book);
      log.info("stored: " + id + "->" + book);
      log.info("EventResource firing readProcessEvent");
      readProcessEvent.fire("readProcessEvent");
      log.info("leaving EventResource.createBook()");
      return Response.ok(id).build();
   }
   
   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   public Book lookupBookById(@PathParam("id") int id)
   {
      log.info("entering EventResource.lookupBookById(" + id + ")");
      log.info("books: " + collection);
      log.info("EventResource firing processEvent");
      processEvent.fire("processEvent");
      Book book = collection.get(id);
      log.info("EventResource firing writeProcessEvent");
      writeProcessEvent.fire("writeProcessEvent");
      if (book == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      log.info("leaving EventResource.lookupBookById(" + id + ")");
      return book;
   }
}
