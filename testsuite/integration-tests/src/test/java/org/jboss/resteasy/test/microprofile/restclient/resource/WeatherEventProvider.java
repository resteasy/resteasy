package org.jboss.resteasy.test.microprofile.restclient.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Provider
public class WeatherEventProvider implements MessageBodyReader<WeatherEvent> {
    @Override
    public boolean isReadable(Class<?>type, Type genericType, Annotation[] annotations,
                              MediaType mediaType){
        return WeatherEvent.class.isAssignableFrom(type)
                && MediaType.SERVER_SENT_EVENTS.equalsIgnoreCase(mediaType.toString());
    }
    @Override
    public
    WeatherEvent readFrom(Class<WeatherEvent>type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String,String> httpHeaders,
                          InputStream entityStream)throws IOException, WebApplicationException {

        InputStreamReader isr = new InputStreamReader(entityStream);
        BufferedReader br = new BufferedReader(isr);
        List<String> lines = new ArrayList<>();
        String l = null;
        while ((l = br.readLine()) != null) {
            lines.add(l);
        }
        br.close();
        if (lines.size() != 2) {
            throw new WebApplicationException("error in " + this.getClass().getSimpleName() +
                    " reading WeatherEvent data");
        }
        return new WeatherEvent(new Date(lines.get(0)), lines.get(1));
    }
}
