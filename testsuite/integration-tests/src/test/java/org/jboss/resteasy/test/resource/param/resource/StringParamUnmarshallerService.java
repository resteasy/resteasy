package org.jboss.resteasy.test.resource.param.resource;

import java.util.Calendar;
import java.util.Date;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.test.resource.param.StringParamUnmarshallerTest;
import org.junit.jupiter.api.Assertions;

@Path("/")
public class StringParamUnmarshallerService {
    @GET
    @Produces("text/plain")
    @Path("/datetest/{date}")
    public String get(
            @PathParam("date") @StringParamUnmarshallerTest.StringParamUnmarshallerDateFormat("MM-dd-yyyy") Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Assertions.assertEquals(3, c.get(Calendar.MONTH), "Wrong date");
        Assertions.assertEquals(23, c.get(Calendar.DAY_OF_MONTH), "Wrong date");
        Assertions.assertEquals(1977, c.get(Calendar.YEAR), "Wrong date");
        return date.toString();
    }

    @GET
    @Produces("text/plain")
    @Path("fromstring/{fruit}/{sport}")
    public String getFromString(@PathParam("fruit") StringParamUnmarshallerFruit fruit,
            @PathParam("sport") StringParamUnmarshallerSport sport) {
        Assertions.assertEquals(fruit, StringParamUnmarshallerFruit.ORANGE, "Wrong fruit");
        Assertions.assertEquals("football", sport.name, "Wrong sport");
        return sport.name + fruit;
    }
}
