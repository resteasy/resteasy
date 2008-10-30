package org.jboss.resteasy.springmvc.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.springmvc.test.jaxb.BasicJaxbObject;

@Path("/basic")
public interface BasicResource {

	/** test basic setup **/
	@GET
	@Produces("text/plain")
	public String getBasicString();

	/** test JAXB **/
	@GET
	@Produces("application/json")
	public BasicJaxbObject getBasicObject();

	/** test Spring MVC ModelAndView **/
	@GET
	@Produces("application/custom")
	@Path("/custom-rep")
	public String getCustomRepresentation();

	/** test Spring @Context injection **/
	@GET
	@Produces("text/plain")
	@Path("/url")
	public String getURL();

	/** test singleton with custom registration **/
	@GET
	@Produces("text/plain")
	@Path("/singleton/count")
	public Integer getSingletonCount();

	/** test prototype with custom registration **/
	@GET
	@Produces("text/plain")
	@Path("/prototype/count")
	public Integer getPrototypeCount();
}
