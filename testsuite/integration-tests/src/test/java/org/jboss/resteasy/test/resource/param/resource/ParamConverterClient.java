package org.jboss.resteasy.test.resource.param.resource;

import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public interface ParamConverterClient {
    @Path("{pojo}")
    @PUT
    void put(@QueryParam("pojo") String q, @PathParam("pojo") String pp, @MatrixParam("pojo") String mp,
             @HeaderParam("pojo") String hp);
    
	@GET
	@Path("multiValuedQueryParam")
	String multiValuedQueryParam(@QueryParam("dates") String dates) ;

	@GET
	@Path("singleValuedQueryParam")
	String singleValuedQueryParam(@QueryParam("date") SortedSet<String> dates) ;
}
