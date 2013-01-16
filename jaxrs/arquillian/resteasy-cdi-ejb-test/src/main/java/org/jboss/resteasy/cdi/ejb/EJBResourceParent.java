package org.jboss.resteasy.cdi.ejb;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.cdi.util.Constants;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 29, 2012
 */
@Path("/")
public interface EJBResourceParent
{
   @GET
   @Path("verifyScopes")
   public int verifyScopes();
   
   @GET
   @Path("verifyInjection")
   public int verifyInjection();
   
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   public int createBook(Book book);
   
   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   public Book lookupBookById(@PathParam("id") int id);
   
   @GET
   @Path("uses/{count}")
   public int testUse(@PathParam("count") int count);
   
   @GET
   @Path("reset")
   public void reset();
}

