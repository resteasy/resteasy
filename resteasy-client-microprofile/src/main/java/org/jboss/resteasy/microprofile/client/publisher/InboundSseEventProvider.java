package org.jboss.resteasy.microprofile.client.publisher;

import org.jboss.resteasy.plugins.providers.sse.SseConstants;
import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.InboundSseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class InboundSseEventProvider implements MessageBodyReader<InboundSseEvent> {
    @Override
    public boolean isReadable(Class<?>type, Type genericType, Annotation[] annotations,
                              MediaType mediaType){
        return InboundSseEvent.class.isAssignableFrom(type)
                && MediaType.SERVER_SENT_EVENTS.equalsIgnoreCase(mediaType.toString());
    }
    @Override
    public InboundSseEvent readFrom(Class<InboundSseEvent>type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String,String> httpHeaders,
                          InputStream entityStream)throws IOException, WebApplicationException {

        int cnt = entityStream.available();
        byte[] byteBuffer = new byte[cnt];
        ((ByteArrayInputStream)entityStream).read(byteBuffer, 0 ,cnt);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(byteBuffer);
        out.write(SseConstants.DOUBLE_EOL);
        ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());

        SseEventInputImpl sseEventInputImpl = new SseEventInputImpl(annotations,
                mediaType, mediaType, httpHeaders, inStream);

        InboundSseEvent inboundSseEvent = sseEventInputImpl.read();
        inStream.close();
        return inboundSseEvent;
    }
}
