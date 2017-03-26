package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
	@Path("singleValuedQueryParam_list")
	String singleValuedQueryParam_list(@QueryParam("date") List<String> dates);
	
	@GET
	@Path("singleValuedQueryParam_arrayList")
	String singleValuedQueryParam_arrayList(@QueryParam("date") ArrayList<String> dates) ;

	@GET
	@Path("singleValuedQueryParam_set")
	String singleValuedQueryParam_set(@QueryParam("date")  Set<String> dates) ;
	
	@GET
	@Path("singleValuedQueryParam_hashSet")
	String singleValuedQueryParam_hashSet(@QueryParam("date")  HashSet<String> dates) ;
	
	@GET
	@Path("singleValuedQueryParam_sortedSet")
	String singleValuedQueryParam_sortedSet(@QueryParam("date")  SortedSet<String> dates) ;
	
	@GET
	@Path("singleValuedQueryParam_treeSet")
	String singleValuedQueryParam_treeSet(@QueryParam("date")TreeSet<String> dates) ;
}
