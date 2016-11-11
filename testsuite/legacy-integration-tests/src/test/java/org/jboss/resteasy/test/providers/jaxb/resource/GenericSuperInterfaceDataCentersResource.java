package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/datacenters")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface GenericSuperInterfaceDataCentersResource {

    @GET
    @Formatted
    GenericSuperInterfaceDataCenters list();

    @POST
    @Formatted
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response add(GenericSuperInterfaceDataCenter dataCenter);

    @DELETE
    @Path("{id}")
    Response remove(@PathParam("id") String id);

    @DELETE
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{id}")
    Response remove(@PathParam("id") String id, GenericSuperInterfaceAction action);

    /**
     * Sub-resource locator method, returns individual GenericSuperInterfaceDataCenterResource on which the
     * remainder of the URI is dispatched.
     *
     * @param id the GenericSuperInterfaceDataCenter ID
     * @return matching subresource if found
     */
    @Path("{id}")
    GenericSuperInterfaceDataCenterResource getDataCenterSubResource(@PathParam("id") String id);
}
