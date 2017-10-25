package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface MultiValuedParamResourceClient {

	@Path("queryParam")
	QueryParamResourceClient queryParam();

	@Path("headerParam")
	HeaderParamResourceClient headerParam();

	@Path("matrixParam")
	MatrixParamResourceClient matrixParam();

	@Path("cookieParam")
	CookieParamResourceClient cookieParam();
	
	@Path("formParam")
	FormParamResourceClient formParam();
	
	@Path("pathParam")
	PathParamResourceClient pathParam();

	@Path("/")
	public interface QueryParamResourceClient {

		@GET
		@Path("customConversion_multiValuedParam")
		String customConversion_multiValuedParam(@QueryParam("date") String dates);
		
		@GET
		@Path("customConversion_multiValuedParam_array")
		String customConversion_multiValuedParam_array(@QueryParam("date") String dates);

		@GET
		@Path("defaultConversion_list")
		String defaultConversion_list(@QueryParam("date") List<String> dates);

		@GET
		@Path("defaultConversion_arrayList")
		String defaultConversion_arrayList(@QueryParam("date") ArrayList<String> dates);

		@GET
		@Path("defaultConversion_set")
		String defaultConversion_set(@QueryParam("date") Set<String> dates);

		@GET
		@Path("defaultConversion_hashSet")
		String defaultConversion_hashSet(@QueryParam("date") HashSet<String> dates);

		@GET
		@Path("defaultConversion_sortedSet")
		String defaultConversion_sortedSet(@QueryParam("date") SortedSet<String> dates);

		@GET
		@Path("defaultConversion_treeSet")
		String defaultConversion_treeSet(@QueryParam("date") TreeSet<String> dates);
		
		@GET
		@Path("defaultConversion_array")
		String defaultConversion_array(@QueryParam("date") String[] dates);

	}

	@Path("/")
	public interface HeaderParamResourceClient {

		@GET
		@Path("customConversion_multiValuedParam")
		String customConversion_multiValuedParam(@HeaderParam("date") String dates);
		
		@GET
		@Path("customConversion_multiValuedParam_array")
		String customConversion_multiValuedParam_array(@HeaderParam("date") String dates);

		@GET
		@Path("defaultConversion_list")
		String defaultConversion_list(@HeaderParam("date") List<String> dates);

		@GET
		@Path("defaultConversion_arrayList")
		String defaultConversion_arrayList(@HeaderParam("date") ArrayList<String> dates);

		@GET
		@Path("defaultConversion_set")
		String defaultConversion_set(@HeaderParam("date") Set<String> dates);

		@GET
		@Path("defaultConversion_hashSet")
		String defaultConversion_hashSet(@HeaderParam("date") HashSet<String> dates);

		@GET
		@Path("defaultConversion_sortedSet")
		String defaultConversion_sortedSet(@HeaderParam("date") SortedSet<String> dates);

		@GET
		@Path("defaultConversion_treeSet")
		String defaultConversion_treeSet(@HeaderParam("date") TreeSet<String> dates);
		
		@GET
		@Path("defaultConversion_array")
		String defaultConversion_array(@HeaderParam("date") String[] dates);

	}

	@Path("/")
	public interface MatrixParamResourceClient {

		@GET
		@Path("customConversion_multiValuedParam")
		String customConversion_multiValuedParam(@MatrixParam("date") String dates);
		
		@GET
		@Path("customConversion_multiValuedParam_array")
		String customConversion_multiValuedParam_array(@MatrixParam("date") String dates);

		@GET
		@Path("defaultConversion_list")
		String defaultConversion_list(@MatrixParam("date") List<String> dates);

		@GET
		@Path("defaultConversion_arrayList")
		String defaultConversion_arrayList(@MatrixParam("date") ArrayList<String> dates);

		@GET
		@Path("defaultConversion_set")
		String defaultConversion_set(@MatrixParam("date") Set<String> dates);

		@GET
		@Path("defaultConversion_hashSet")
		String defaultConversion_hashSet(@MatrixParam("date") HashSet<String> dates);

		@GET
		@Path("defaultConversion_sortedSet")
		String defaultConversion_sortedSet(@MatrixParam("date") SortedSet<String> dates);

		@GET
		@Path("defaultConversion_treeSet")
		String defaultConversion_treeSet(@MatrixParam("date") TreeSet<String> dates);
		
		@GET
		@Path("defaultConversion_array")
		String defaultConversion_array(@MatrixParam("date") String[] dates);

	}

	@Path("/")
	public interface CookieParamResourceClient {

		@GET
		@Path("customConversion_multiValuedCookieParam")
		String customConversion_multiValuedCookieParam(@CookieParam("date") String dates);
		
		@GET
		@Path("customConversion_multiValuedCookieParam_array")
		String customConversion_multiValuedCookieParam_array(@CookieParam("date") String dates);

		@GET
		@Path("defaultConversion_list")
		String defaultConversion_list(@CookieParam("date") String dates);

		@GET
		@Path("defaultConversion_arrayList")
		String defaultConversion_arrayList(@CookieParam("date") String dates);

		@GET
		@Path("defaultConversion_set")
		String defaultConversion_set(@CookieParam("date") String dates);

		@GET
		@Path("defaultConversion_hashSet")
		String defaultConversion_hashSet(@CookieParam("date") String dates);

		@GET
		@Path("defaultConversion_sortedSet")
		String defaultConversion_sortedSet(@CookieParam("date") String dates);

		@GET
		@Path("defaultConversion_treeSet")
		String defaultConversion_treeSet(@CookieParam("date") String dates);
		
		@GET
		@Path("defaultConversion_array")
		String defaultConversion_array(@CookieParam("date") String dates);

	}
	
	@Path("/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public interface FormParamResourceClient {

		@POST
		@Path("customConversion_multiValuedParam")
		String customConversion_multiValuedParam(@FormParam("date") String dates);
		
		@POST
		@Path("customConversion_multiValuedParam_array")
		String customConversion_multiValuedParam_array(@FormParam("date") String dates);

		@POST
		@Path("defaultConversion_list")
		String defaultConversion_list(@FormParam("date") List<String> dates);

		@POST
		@Path("defaultConversion_arrayList")
		String defaultConversion_arrayList(@FormParam("date") ArrayList<String> dates);

		@POST
		@Path("defaultConversion_set")
		String defaultConversion_set(@FormParam("date") Set<String> dates);

		@POST
		@Path("defaultConversion_hashSet")
		String defaultConversion_hashSet(@FormParam("date") HashSet<String> dates);

		@POST
		@Path("defaultConversion_sortedSet")
		String defaultConversion_sortedSet(@FormParam("date") SortedSet<String> dates);

		@POST
		@Path("defaultConversion_treeSet")
		String defaultConversion_treeSet(@FormParam("date") TreeSet<String> dates);
		
		@POST
		@Path("defaultConversion_array")
		String defaultConversion_array(@FormParam("date") String[] dates);

	}
	
	@Path("/")
	public interface PathParamResourceClient {

		@GET
		@Path("customConversion_multiValuedPathParam/{path1}/{path2}/{path3}")
		String customConversion_multiValuedPathParam(@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3);
		
		@GET
		@Path("customConversion_multiValuedPathParam_array/{path1}/{path2}/{path3}")
		String customConversion_multiValuedPathParam_array(@PathParam("path1") String path1, @PathParam("path2") String path2, @PathParam("path3") String path3);

		@GET
		@Path("defaultConversion_list/{path}")
		String defaultConversion_list(@PathParam("path") String path);

		@GET
		@Path("defaultConversion_arrayList/{path}")
		String defaultConversion_arrayList(@PathParam("path") String path);

		@GET
		@Path("defaultConversion_set/{path}")
		String defaultConversion_set(@PathParam("path") String path);

		@GET
		@Path("defaultConversion_hashSet/{path}")
		String defaultConversion_hashSet(@PathParam("path") String path);

		@GET
		@Path("defaultConversion_sortedSet/{path}")
		String defaultConversion_sortedSet(@PathParam("path") String path);

		@GET
		@Path("defaultConversion_treeSet/{path}")
		String defaultConversion_treeSet(@PathParam("path") String path);
		
		@GET
		@Path("defaultConversion_array/{path}")
		String defaultConversion_array(@PathParam("path") String path);

	}

}
