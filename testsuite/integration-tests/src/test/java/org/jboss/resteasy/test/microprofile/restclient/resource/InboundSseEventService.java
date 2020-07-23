package org.jboss.resteasy.test.microprofile.restclient.resource;

import io.reactivex.Flowable;
import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.SseEventProvider;
import org.reactivestreams.Publisher;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEvent;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Path("theService")
public class InboundSseEventService {

    @GET
    @Path("events")
    @Produces({MediaType.SERVER_SENT_EVENTS})
    public Publisher<InboundSseEvent> getEvents() {
        List<InboundSseEvent> outList = generatedInBoundWeatherEvents();
        return Flowable.fromArray(outList.get(0), outList.get(1), outList.get(2));
    }

    public static List<InboundSseEvent> generatedInBoundWeatherEvents() {
        List<WeatherEvent> eventList = generatedWeatherEvents();
        List<InboundSseEvent> inList = new ArrayList<>();

        int cnt = 1;
        for(WeatherEvent wEvent : eventList){
            LocalInboundSseEvent eventBuilder = new LocalInboundSseEvent();
            eventBuilder.setName("wEvent-name-"+cnt);
            eventBuilder.setId("event-"+cnt++);
            eventBuilder.setComment("Comment line one.\nComment line two.");
            eventBuilder.setData(wEvent.toString().getBytes());
            inList.add(eventBuilder);
        }

        return inList;
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

    static class LocalInboundSseEvent implements InboundSseEvent {
        private String name = null;
        private String id = null;
        private String comment = null;
        private byte[] data = null;
        private long reconnectDelay = SseEvent.RECONNECT_NOT_SET;
        private Annotation[] annotations = null;
        private MediaType mediaType = null;

        public String toString () {
            OutboundSseEventImpl.BuilderImpl builder = new OutboundSseEventImpl.BuilderImpl();
            builder.name(name);
            builder.id(id);
            builder.comment(comment);
            builder.reconnectDelay(reconnectDelay);
            builder.data(data);
            OutboundSseEvent event = builder.build();
            SseEventProvider provider = new SseEventProvider();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try {
                provider.writeTo(event, event.getClass(), null,
                        new Annotation[]{}, MediaType.TEXT_PLAIN_TYPE, null, bout);
            } catch (Exception e) {
                throw new RuntimeException("InboundSseEventService: " + e);
            }
            return new String(bout.toByteArray(), StandardCharsets.UTF_8);
        }

        @Override
        public boolean isEmpty(){
            return data.length == 0;
        }

        public void setId(String id) {
            this.id = id;
        }
        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public long getReconnectDelay() {
            if (reconnectDelay < 0)
            {
                return -1;
            }
            return reconnectDelay;
        }

        public void setReconnectDelay(int n) {
            this.reconnectDelay = n;
        }

        @Override
        public boolean isReconnectDelaySet() {
            return reconnectDelay > -1;
        }

        public void setData(byte[] data) {
                this.data = data;
        }

        ////////////////////////////////////////////////////
        @Override
        public String readData() {
            return new String(data, StandardCharsets.UTF_8);
        }

        @Override
        public<T> T readData(Class<T> type) {
            return null; // no-op
        }

        @Override
        public<T> T readData(GenericType<T> type) {
            return null; // no-op
        }

        @Override
        public<T> T readData(Class<T> messageType, MediaType mediaType) {
            return null; // no-op
        }

        @Override
        public <T> T readData(GenericType<T> type, MediaType mediaType) {
            return null; // no-op
        }
    }
}
