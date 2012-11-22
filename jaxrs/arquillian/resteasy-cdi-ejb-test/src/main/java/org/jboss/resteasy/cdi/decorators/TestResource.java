package org.jboss.resteasy.cdi.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
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
 * Copyright Nov 13, 2012
 */
@Path("/")
@RequestScoped
public class TestResource implements TestResourceIntf
{  
   static private Map<Integer, Book> collection = new HashMap<Integer, Book>();
   static private AtomicInteger counter = new AtomicInteger();

   @Inject private Logger log;

   @Override
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @FilterBinding
   @ResourceBinding
   public Response createBook(Book book)
   {
      log.info("entering TestResource.createBook()");
      VisitList.add(VisitList.RESOURCE_ENTER);
      int id = counter.getAndIncrement();
      book.setId(id);
      collection.put(id, book);
      log.info("stored: " + id + "->" + book);
      VisitList.add(VisitList.RESOURCE_LEAVE);
      log.info("leaving TestResource.createBook()");
      return Response.ok(id).build();
   }

   @Override
   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   @FilterBinding
   @ResourceBinding
   public Book lookupBookById(@PathParam("id") int id)
   {
      log.info("entering TestResource.lookupBookById(" + id + ")");
      VisitList.add(VisitList.RESOURCE_ENTER);
      log.info("books: " + collection);
      Book book = collection.get(id);
      if (book == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      VisitList.add(VisitList.RESOURCE_LEAVE);
      log.info("leaving TestResource.lookupBookById(" + id + ")");
      return book;
   }

   @Override
   @POST
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   public Response test()
   {
      log.info("entering TestResource.test()");
      ArrayList<String> expectedList = new ArrayList<String>();
      
      // Call to createBook()
      expectedList.add(VisitList.REQUEST_FILTER_DECORATOR_ENTER);
      expectedList.add(VisitList.REQUEST_FILTER_DECORATOR_LEAVE);
      expectedList.add(VisitList.READER_INTERCEPTOR_DECORATOR_ENTER);
      expectedList.add(VisitList.READER_INTERCEPTOR_ENTER);
      expectedList.add(VisitList.READER_DECORATOR_ENTER);
      expectedList.add(VisitList.READER_DECORATOR_LEAVE);
      expectedList.add(VisitList.READER_INTERCEPTOR_LEAVE);
      expectedList.add(VisitList.READER_INTERCEPTOR_DECORATOR_LEAVE);
      expectedList.add(VisitList.RESOURCE_INTERCEPTOR_ENTER);
      expectedList.add(VisitList.RESOURCE_DECORATOR_ENTER);
      expectedList.add(VisitList.RESOURCE_ENTER);
      expectedList.add(VisitList.RESOURCE_LEAVE);
      expectedList.add(VisitList.RESOURCE_DECORATOR_LEAVE);
      expectedList.add(VisitList.RESOURCE_INTERCEPTOR_LEAVE);
      expectedList.add(VisitList.RESPONSE_FILTER_DECORATOR_ENTER);
      expectedList.add(VisitList.RESPONSE_FILTER_DECORATOR_LEAVE);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_DECORATOR_ENTER);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_ENTER);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_LEAVE);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_DECORATOR_LEAVE);
      
      // Call to lookupBookById()
      expectedList.add(VisitList.REQUEST_FILTER_DECORATOR_ENTER);
      expectedList.add(VisitList.REQUEST_FILTER_DECORATOR_LEAVE);
      expectedList.add(VisitList.RESOURCE_INTERCEPTOR_ENTER);
      expectedList.add(VisitList.RESOURCE_DECORATOR_ENTER);
      expectedList.add(VisitList.RESOURCE_ENTER);
      expectedList.add(VisitList.RESOURCE_LEAVE);
      expectedList.add(VisitList.RESOURCE_DECORATOR_LEAVE);
      expectedList.add(VisitList.RESOURCE_INTERCEPTOR_LEAVE);
      expectedList.add(VisitList.RESPONSE_FILTER_DECORATOR_ENTER);
      expectedList.add(VisitList.RESPONSE_FILTER_DECORATOR_LEAVE);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_DECORATOR_ENTER);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_ENTER);
      expectedList.add(VisitList.WRITER_DECORATOR_ENTER);
      expectedList.add(VisitList.WRITER_DECORATOR_LEAVE);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_LEAVE);
      expectedList.add(VisitList.WRITER_INTERCEPTOR_DECORATOR_LEAVE);
      
      // Call to test()
      expectedList.add(VisitList.READER_INTERCEPTOR_DECORATOR_ENTER);
      expectedList.add(VisitList.READER_INTERCEPTOR_ENTER);
      expectedList.add(VisitList.READER_DECORATOR_ENTER);
      expectedList.add(VisitList.READER_DECORATOR_LEAVE);
      expectedList.add(VisitList.READER_INTERCEPTOR_LEAVE);
      expectedList.add(VisitList.READER_INTERCEPTOR_DECORATOR_LEAVE);
      ArrayList<String> visitList = VisitList.getList();
      for (int i = 0; i < visitList.size(); i++)
      {
         log.info(i + ": " + visitList.get(i).toString());
      }
      boolean status = expectedList.size() == visitList.size();
      if (!status)
      {
         log.info("expectedList.size() [" + expectedList.size() + "] != visitList.size() [" + visitList.size() + "]");
      }
      else
      {
         for (int i = 0; i < expectedList.size(); i++)
         {
            if (!expectedList.get(i).equals(visitList.get(i)))
            {
               status = false;
               log.info("visitList.get(" + i + ") incorrect: should be: " + expectedList.get(i) + ", is: " + visitList.get(i));
               break;
            }
         }
      }
      log.info("leaving TestResource.test()");
      return status ? Response.ok().build() : Response.serverError().build();
   }
}
