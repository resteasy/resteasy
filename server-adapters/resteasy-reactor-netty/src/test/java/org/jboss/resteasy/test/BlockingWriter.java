package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.reactor.netty.NettyUtil;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

@Provider
public class BlockingWriter implements MessageBodyWriter<BlockingWriterData> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(BlockingWriterData t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        String resp = NettyUtil.isIoThread() ? "KO" : "OK";
        entityStream.write(resp.getBytes(Charset.forName("UTF-8")));
        entityStream.close();

        // in IO thread / blocking writer: suspend, do IO on worker and complete() on done if suspended
        // in IO thread / non-blocking writer: just run
        // in non-IO thread / blocking writer: invoke (caller calls finish)
        // in non-IO thread / non-blocking writer: block until writing is done (caller calls finish)

        // for suspended request
        // in IO thread / blocking writer: check who called the writer
    }

}
