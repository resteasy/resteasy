package org.jboss.resteasy.springmvc.test.resources;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.springmvc.test.jaxb.BasicJaxbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Path("/basic")
@Component
public class BasicResourceImpl {

	private BasicDao basicDao;
	private HttpHeaders headers;

	/**
	 * Note, this is Spring functionality not JAX-RS Core, but still using
	 * JAX-RS @Context values. You don't really need @Context for spring to do
	 * this
	 * 
	 * @param basicDao
	 * @param headers
	 */
	@Autowired
	public BasicResourceImpl(BasicDao basicDao, @Context HttpHeaders headers) {
		super();
		this.basicDao = basicDao;
		this.headers = headers;
	}

	/** really simple test */
	@GET
	@Produces("text/plain")
	public String getBasicString() {
		return "test";
	}

	@GET
	@Produces("application/json")
	public BasicJaxbObject getBasicObject() {
		return new BasicJaxbObject("test", new Date());
	}

	/** WOOHOO!  SpringMVC ModelAndView in action */
	@GET
	@Produces("application/custom")
	@Path("/custom-rep")
	public ModelAndView getCustomRepresentation() {
		// MyCustomView is auto created 
		return new ModelAndView("myCustomView");
	}

	/** */
	@GET
	@Produces("text/plain")
	@Path("/header")
	public String getContentTypeHeader() {
		return this.headers.getAcceptableMediaTypes().get(0).toString();
	}

	/** the dao knows the path via an @Context inject value */
	@GET
	@Produces("text/plain")
	@Path("/url")
	public String getURL() {
		return basicDao.getPath();
	}

}
