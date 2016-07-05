package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.test.resource.param.StringParamUnmarshallerTest;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Calendar;
import java.util.Date;

@Path("/")
public class StringParamUnmarshallerService {
    @GET
    @Produces("text/plain")
    @Path("/datetest/{date}")
    public String get(@PathParam("date") @StringParamUnmarshallerTest.StringParamUnmarshallerDateFormat("MM-dd-yyyy") Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Assert.assertEquals("Wrong date", 3, c.get(Calendar.MONTH));
        Assert.assertEquals("Wrong date", 23, c.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals("Wrong date", 1977, c.get(Calendar.YEAR));
        return date.toString();
    }

    @GET
    @Produces("text/plain")
    @Path("fromstring/{fruit}/{sport}")
    public String getFromString(@PathParam("fruit") StringParamUnmarshallerFruit fruit, @PathParam("sport") StringParamUnmarshallerSport sport) {
        Assert.assertEquals("Wrong fruit", fruit, StringParamUnmarshallerFruit.ORANGE);
        Assert.assertEquals("Wrong sport", "football", sport.name);
        return sport.name + fruit;
    }
}
