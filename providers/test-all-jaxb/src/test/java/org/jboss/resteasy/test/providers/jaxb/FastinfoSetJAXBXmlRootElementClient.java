package org.jboss.resteasy.test.providers.jaxb;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * A JAXBXmlRootElementClient.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Consumes("application/fastinfoset")
@Produces("application/fastinfoset")
public interface FastinfoSetJAXBXmlRootElementClient
{

   @GET
   @Path("/{name}")
   Parent getParent(@PathParam("name")
   String name);

   @POST
   Parent postParent(Parent parent);

}