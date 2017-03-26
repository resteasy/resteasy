package org.jboss.resteasy.test.resource.param.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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
import javax.ws.rs.core.Response;

import org.junit.Assert;

@Path("/")
public class ParamConverterResource {
    @Path("{pojo}")
    @PUT
    public void put(@QueryParam("pojo") ParamConverterPOJO q, @PathParam("pojo") ParamConverterPOJO pp, @MatrixParam("pojo") ParamConverterPOJO mp,
                    @HeaderParam("pojo") ParamConverterPOJO hp) {
        Assert.assertEquals(q.getName(), "pojo");
        Assert.assertEquals(pp.getName(), "pojo");
        Assert.assertEquals(mp.getName(), "pojo");
        Assert.assertEquals(hp.getName(), "pojo");
    }
    
	// This method can only handle one 'dates' query parameter
	@GET
	@Path("multiValuedQueryParam")
	public Response multiValuedQueryParam(@QueryParam("dates") LinkedList<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam_list")
	public Response singleValuedQueryParam_list(@QueryParam("date") List<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam_arrayList")
	public Response singleValuedQueryParam_arrayList(@QueryParam("date") ArrayList<Date> dates) {
		return Response.ok(format(dates)).build();
	}

	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam_set")
	public Response singleValuedQueryParam_set(@QueryParam("date") Set<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam_hashSet")
	public Response singleValuedQueryParam_hashSet(@QueryParam("date") HashSet<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam_sortedSet")
	public Response singleValuedQueryParam_sortedSet(@QueryParam("date") SortedSet<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam_treeSet")
	public Response singleValuedQueryParam_treeSet(@QueryParam("date") TreeSet<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	private static String format(Collection<Date> dates) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		StringBuilder stringBuilder = new StringBuilder();
		int datesCount = dates.size();
		int i = 0;
		for (Date date : dates) {
			stringBuilder.append(simpleDateFormat.format(date));
			++i;
			if (i < datesCount) {
				stringBuilder.append(", ");
			}
		}
		return stringBuilder.toString();
	}
	
}
