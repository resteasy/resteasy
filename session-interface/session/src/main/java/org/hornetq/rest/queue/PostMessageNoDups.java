package org.hornetq.rest.queue;

import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * implements reliable "create", "create-next" pattern defined by REST-* Messaging specificaiton
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMessageNoDups extends PostMessage
{

   @POST
   public Response redirectCreation(@Context UriInfo uriInfo)
   {
      String id = generateDupId();
      Response.ResponseBuilder res = Response.status(Response.Status.TEMPORARY_REDIRECT.getStatusCode());
      res.location(uriInfo.getAbsolutePathBuilder().path(id).build());
      return res.build();
   }

}