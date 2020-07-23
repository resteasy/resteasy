package org.jboss.resteasy.test.microprofile.restclient.resource;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Path("theService")
public class WeatherEventService {

    @GET
    @Path("events")
    @Produces({MediaType.SERVER_SENT_EVENTS})
    public Publisher<WeatherEvent> getEvents() {
        List<WeatherEvent> outList = generatedWeatherEvents();
        return Flowable.fromArray(outList.get(0), outList.get(1), outList.get(2));
    }

    @GET
    @Path("eventsJson")
    @Produces("text/event-stream;element-type=application/json")
    public Publisher<WeatherEvent> getEventsJson() {
        List<WeatherEvent> outList = generatedWeatherEvents();
        return Flowable.fromArray(outList.get(0), outList.get(1), outList.get(2));
    }

    /**
     * Method is public so test can retrieve the same values to check against
     * @return
     */
    public static List<WeatherEvent> generatedWeatherEvents() {
        ArrayList<WeatherEvent> eventList = new ArrayList<WeatherEvent>();
        Calendar calendar = new GregorianCalendar(2000, 8, 4);
        eventList.add(new WeatherEvent(calendar.getTime(), "Sunny"));
        calendar.add(Calendar.YEAR, 5);
        eventList.add(new WeatherEvent(calendar.getTime(), "Overcast"));
        calendar.add(Calendar.YEAR, 10);
        eventList.add(new WeatherEvent(calendar.getTime(), "Cloudy"));
        return eventList;
    }

}
