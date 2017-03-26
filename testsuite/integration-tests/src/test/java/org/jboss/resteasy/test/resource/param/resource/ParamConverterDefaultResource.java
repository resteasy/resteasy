package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;
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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/")
public class ParamConverterDefaultResource {
    @PUT
    public void putDefault(@QueryParam("pojo") @DefaultValue("default") ParamConverterPOJO q,
                           @MatrixParam("pojo") @DefaultValue("default") ParamConverterPOJO mp, @DefaultValue("default") @HeaderParam("pojo") ParamConverterPOJO hp) {
        Assert.assertEquals(q.getName(), "default");
        Assert.assertEquals(mp.getName(), "default");
        Assert.assertEquals(hp.getName(), "default");
    }
    
	// This method can only handle one 'dates' query parameter
	@GET
	@Path("default/multiValuedQueryParam")
	public Response multiValuedQueryParam(@QueryParam("dates")@DefaultValue("20161214, 20161215, 20161216") LinkedList<Date> dates) {
		return Response.ok(format(dates)).build();
	}

	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam_list")
	public Response singleValuedQueryParam_list(@QueryParam("date") @DefaultValue("20161214") List<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam_arrayList")
	public Response singleValuedQueryParam_arrayList(@QueryParam("date") @DefaultValue("20161214") ArrayList<Date> dates) {
		return Response.ok(format(dates)).build();
	}

	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam_set")
	public Response singleValuedQueryParam_set(@QueryParam("date") @DefaultValue("20161214") Set<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam_hashSet")
	public Response singleValuedQueryParam_hashSet(@QueryParam("date") @DefaultValue("20161214") HashSet<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam_sortedSet")
	public Response singleValuedQueryParam_sortedSet(@QueryParam("date") @DefaultValue("20161214") SortedSet<Date> dates) {
		return Response.ok(format(dates)).build();
	}
	
	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam_treeSet")
	public Response singleValuedQueryParam_treeSet(@QueryParam("date") @DefaultValue("20161214") TreeSet<Date> dates) {
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
