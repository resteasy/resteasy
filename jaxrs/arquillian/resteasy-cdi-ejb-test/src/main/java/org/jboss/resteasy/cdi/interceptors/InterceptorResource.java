package org.jboss.resteasy.cdi.interceptors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
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
@Interceptors ({Interceptor0.class})
@ClassBinding
public class InterceptorResource
{  
   static private Map<Integer, Book> collection = new HashMap<Integer, Book>();
   static private AtomicInteger counter = new AtomicInteger();
   
   @Inject private Logger log;
   
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @Interceptors ({Interceptor1.class})
   @MethodBinding
   @FilterBinding
   public Response createBook(Book book)
   {
      log.info("entering InterceptorResource.createBook()");
      int id = counter.getAndIncrement();
      book.setId(id);
      collection.put(id, book);
      log.info("stored: " + id + "->" + book);
      log.info("leaving InterceptorResource.createBook()");
      return Response.ok(id).build();
   }
   
   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   @Interceptors ({Interceptor1.class})
   @MethodBinding
   @FilterBinding
   public Book lookupBookById(@PathParam("id") int id)
   {
      log.info("entering InterceptorResource.lookupBookById(" + id + ")");
      log.info("books: " + collection);
      Book book = collection.get(id);
      if (book == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      log.info("leaving InterceptorResource.lookupBookById(" + id + ")");
      return book;
   }
   
   
   @POST
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   @Interceptors ({Interceptor1.class})
   @MethodBinding
   public Response test()
   {
      log.info("entering InterceptorResource.test()");
      ArrayList<Class<?>> expectedList = new ArrayList<Class<?>>();
      expectedList.add(RequestFilterInterceptor.class);          // TestRequestFilter.filter()
      expectedList.add(Interceptor0.class);                      // BookReader.isReadable()
      expectedList.add(Interceptor2.class);                      // BookReader.isReadable()
      expectedList.add(BookReaderInterceptorInterceptor.class);  // BookReaderInterceptor.aroundReadFrom()
      expectedList.add(BookReaderInterceptor.class);             // BookReader.readFrom()
      expectedList.add(Interceptor0.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor1.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor2.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor3.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor0.class);                      // InterceptorResource.createBook()
      expectedList.add(Interceptor1.class);                      // InterceptorResource.createBook()
      expectedList.add(Interceptor2.class);                      // InterceptorResource.createBook()
      expectedList.add(Interceptor3.class);                      // InterceptorResource.createBook()
      expectedList.add(ResponseFilterInterceptor.class);         // TestResponseFilter.filter()
      expectedList.add(BookWriterInterceptorInterceptor.class);  // BookWriterInterceptor.aroundWriteTo()
      expectedList.add(BookWriterInterceptor.class);             // BookWriter.writeTo()
      expectedList.add(RequestFilterInterceptor.class);          // TestRequestFilter.filter()
      expectedList.add(Interceptor0.class);                      // InterceptorResource.lookBookById()
      expectedList.add(Interceptor1.class);                      // InterceptorResource.lookBookById()
      expectedList.add(Interceptor2.class);                      // InterceptorResource.lookBookById()
      expectedList.add(Interceptor3.class);                      // InterceptorResource.lookBookById()
      expectedList.add(ResponseFilterInterceptor.class);         // TestResponseFilter.filter()
      expectedList.add(Interceptor0.class);                      // BookWriter.isWriteable()
      expectedList.add(Interceptor2.class);                      // BookWriter.isWriteable()
      expectedList.add(Interceptor0.class);                      // BookWriter.getSize()
      expectedList.add(Interceptor2.class);                      // BookWriter.getSize()
      expectedList.add(BookWriterInterceptorInterceptor.class);  // BookWriterInterceptor.aroundWriteTo()
      expectedList.add(BookWriterInterceptor.class);             // BookWriter.writeTo()
      expectedList.add(Interceptor0.class);                      // BookWriter.writeTo()
      expectedList.add(Interceptor1.class);                      // BookWriter.writeTo()
      expectedList.add(Interceptor2.class);                      // BookWriter.writeTo()
      expectedList.add(Interceptor3.class);                      // BookWriter.writeTo()
      expectedList.add(Interceptor0.class);                      // BookReader.isReadable()
      expectedList.add(Interceptor2.class);                      // BookReader.isReadable()
      expectedList.add(BookReaderInterceptorInterceptor.class);  // BookReaderInterceptor.aroundReadFrom()
      expectedList.add(BookReaderInterceptor.class);             // BookReader.readFrom()
      expectedList.add(Interceptor0.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor1.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor2.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor3.class);                      // BookReader.readFrom()
      expectedList.add(Interceptor0.class);                      // InterceptorResource.test()
      expectedList.add(Interceptor1.class);                      // InterceptorResource.test()
      expectedList.add(Interceptor2.class);                      // InterceptorResource.test()
      expectedList.add(Interceptor3.class);                      // InterceptorResource.test()
      
      ArrayList<Object> visitList = VisitList.getList();
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
            if (!expectedList.get(i).isAssignableFrom(visitList.get(i).getClass()))
            {
               status = false;
               log.info("visitList.get(" + i + ") incorrect: should be an instance of: " + expectedList.get(i) + ", is: " + visitList.get(i));
               break;
            }
         }
      }
      log.info("leaving InterceptorResource.test()");
      return status ? Response.ok().build() : Response.serverError().build();
   }
}
