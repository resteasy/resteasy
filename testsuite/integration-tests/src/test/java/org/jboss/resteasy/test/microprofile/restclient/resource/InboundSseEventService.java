package org.jboss.resteasy.test.microprofile.restclient.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("theService")
public class InboundSseEventService {
    @GET
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void sentEvents(@Context SseEventSink sseEventSink, @Context Sse sse) {
        List<WeatherEvent> eventList = generatedWeatherEvents();
        int cnt = 1;
        try (SseEventSink sink = sseEventSink) {

            for (WeatherEvent wEvent : eventList) {
                sseEventSink.send(sse.newEventBuilder()
                        .name("wEvent-name-" + cnt)
                        .id("event-" + cnt++)
                        .comment("Comment line one.\nComment line two.")
                        .data(wEvent.toString().getBytes()).build());
            }
        }
    }

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
