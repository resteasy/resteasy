package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;


@Path("/")
public interface ParamConverterDefaultClient {
    @PUT
    void put();
    
	@GET
	@Path("default/multiValuedQueryParam")
	String multiValuedQueryParam() ;

	@GET
	@Path("default/singleValuedQueryParam_list")
	String singleValuedQueryParam_list();
	
	@GET
	@Path("default/singleValuedQueryParam_arrayList")
	String singleValuedQueryParam_arrayList() ;

	@GET
	@Path("default/singleValuedQueryParam_set")
	String singleValuedQueryParam_set() ;
	
	@GET
	@Path("default/singleValuedQueryParam_hashSet")
	String singleValuedQueryParam_hashSet() ;
	
	@GET
	@Path("default/singleValuedQueryParam_sortedSet")
	String singleValuedQueryParam_sortedSet() ;
	
	@GET
	@Path("default/singleValuedQueryParam_treeSet")
	String singleValuedQueryParam_treeSet() ;
}
