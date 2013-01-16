package org.jboss.resteasy.cdi.decorators;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
public interface TestResourceIntf
{
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @FilterBinding
   @ResourceBinding
   public abstract Response createBook(Book book);

   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   @FilterBinding
   @ResourceBinding
   public abstract Book lookupBookById(@PathParam("id") int id);

   @POST
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   public abstract Response test();

}