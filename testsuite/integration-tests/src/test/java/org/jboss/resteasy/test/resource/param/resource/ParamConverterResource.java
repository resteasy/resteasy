package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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
	public Response multiValuedQueryParam(@QueryParam("dates") List<Date> dates) {
		return Response.ok(format(dates)).build();
	}

	// This method can handle multiple 'date' query parameters
	@GET
	@Path("singleValuedQueryParam")
	public Response singleValuedQueryParam(@QueryParam("date") SortedSet<Date> dates) {
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
