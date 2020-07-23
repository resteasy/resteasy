package org.jboss.resteasy.test.microprofile.restclient.resource;

import javax.annotation.Priority;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.annotation.Annotation;

@Provider
@Priority(Priorities.USER)  // set this to be higher priority an JsonBindingProvider
public class WeatherEventProviderJSON  implements MessageBodyReader<WeatherEvent> {
    @Override
    public boolean isReadable(Class<?>type, Type genericType, Annotation[] annotations,
                              MediaType mediaType){
        return WeatherEvent.class.isAssignableFrom(type)
                && "application/json".equalsIgnoreCase(mediaType.toString());
    }
    @Override
    public
    WeatherEvent readFrom(Class<WeatherEvent>type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String,String> httpHeaders,
                          InputStream entityStream)throws IOException, WebApplicationException {
        JsonReaderFactory factory= Json.createReaderFactory(null);
        JsonReader reader=factory.createReader(entityStream);
        try {
            JsonObject jsonObject=reader.readObject();
            String dateString=jsonObject.getString("date");
            String description=jsonObject.getString("description");
            DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            WeatherEvent event=new WeatherEvent(df.parse(dateString),description);
            return event;
        }catch(ParseException ex){
            throw new IOException(ex);
        }
    }
}
