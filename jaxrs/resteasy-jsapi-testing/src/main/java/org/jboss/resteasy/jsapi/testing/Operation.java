package org.jboss.resteasy.jsapi.testing;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 11 01 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public interface Operation {
    @GET
    @Path("/{operand1}/{operand2}")
    @Produces(MediaType.TEXT_PLAIN)
    public Integer operate(@PathParam("operand1") Integer operand1, @PathParam("operand2") Integer operand2);
}
