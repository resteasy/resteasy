package org.jboss.resteasy.cdi.inheritence;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 5, 2012
 */
@Path("/")
@RequestScoped
public class InheritanceResource
{  
   @Inject private Logger log;
   
   @Inject
   @SelectBook
   private Book book;
   
   @GET
   @Path("vanilla")
   @Produces(MediaType.TEXT_PLAIN)
   public Response vanilla()
   {
      log.info("vanilla(): book is " + book.getClass());
      if (book.getClass().equals(Book.class))
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @GET
   @Path("alternative/vanilla")
   @Produces(MediaType.TEXT_PLAIN)
   public Response alternativeVanilla()
   {
      log.info("alternativeVanilla(): book is " + book.getClass());
      if (book.getClass().equals(Book.class))
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @GET
   @Path("alternative/selected")
   @Produces(MediaType.TEXT_PLAIN)
   public Response alternativeSelected()
   {
      log.info("alternativeSelected(): book is " + book.getClass());
      if (book.getClass().equals(BookSelectedAlternative.class))
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @GET
   @Path("specialized")
   @Produces(MediaType.TEXT_PLAIN)
   public Response specialized()
   {
      log.info("specialized(): book is " + book.getClass());
      if (book.getClass().equals(BookSpecialized.class))
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
}
