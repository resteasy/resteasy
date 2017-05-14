package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

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
	public Response multiValuedQueryParam(@QueryParam("dates")@DefaultValue("20161214, 20161215, 20161216") List<Date> dates) {
		return Response.ok(format(dates)).build();
	}

	// This method can handle multiple 'date' query parameters
	@GET
	@Path("default/singleValuedQueryParam")
	public Response singleValuedQueryParam(@QueryParam("date")@DefaultValue("20161214") SortedSet<Date> dates) {
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
