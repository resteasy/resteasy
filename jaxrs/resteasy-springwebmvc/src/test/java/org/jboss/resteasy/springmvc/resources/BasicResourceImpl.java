package org.jboss.resteasy.springmvc.resources;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.springmvc.test.jaxb.BasicJaxbObject;
import org.springframework.web.servlet.ModelAndView;

@Path("/basic")
public class BasicResourceImpl {

	@Context UriInfo uriInfo;	
	
	@GET
	@Produces("text/plain")
	public String getBasicString() {
		return "test";
	}

	@GET
	@Produces("text/plain")
	@Path("/url")
	public String getURL() {
		return uriInfo.getPath();
	}

	@GET
	@Produces("application/json")
	public BasicJaxbObject getBasicObject() {
		return new BasicJaxbObject("test", new Date());
	}

	@GET
    @Produces("application/custom")
    @Path("/custom-rep")
    public ModelAndView getCustomRepresentation(){
		return new ModelAndView("someView");
	}
}
