package org.resteasy.plugins.providers;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.mail.MessagingException;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * A provider to handle multipart representations. This implementation will be 
 * invoked when a method parameter takes a {@link MimeMultipart} as a method 
 * parameter or a return value and the @ConsumeMime value is either 
 * multipart/mixed or multipart/form-data. 
 * 
 * <code>
 * @POST
 * @ConsumeMime("multipart/form-data")
 * public void postData(MimeMultipart multipart) {
 * ...
 * </code>
 * 
 * When the {@link MimeMultipart} is passed to the method body, it is up to the
 * developer to extract the various parts.
 *  
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 */
@Provider
@ProduceMime("multipart/mixed")
@ConsumeMime({"multipart/mixed", "multipart/form-data"})
public class MimeMultipartProvider
        implements MessageBodyReader<MimeMultipart>,
        MessageBodyWriter<MimeMultipart> {

    /**
     * 
     * @param type
     * @param genericType
     * @param annotations
     * @return
     */
    public boolean isReadable(Class<?> type,
                              Type genericType,
                              Annotation[] annotations) {
        return MimeMultipart.class.equals(type);
    }

    /**
     * 
     * @param type
     * @param genericType
     * @param annotations
     * @return
     */
    public boolean isWriteable(Class<?> type,
                               Type genericType,
                               Annotation[] annotations) {
        return MimeMultipart.class.equals(type);
    }

    /**
     * 
     * @param type
     * @param genericType
     * @param mediaType
     * @param annotations
     * @param httpHeaders
     * @param entityStream
     * @return
     * @throws java.io.IOException
     */
    public MimeMultipart readFrom(Class<MimeMultipart> type, 
                                  Type genericType,
                                  MediaType mediaType, 
                                  Annotation[] annotations,
                                  MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException {
        try {
            if (mediaType != null) {
                mediaType = new MediaType("multipart", "mixed");
            }
            ByteArrayDataSource ds =
                    new ByteArrayDataSource(entityStream, mediaType.toString());
            return new MimeMultipart(ds);
        } catch (MessagingException ex) {
            throw new IOException(""+ex.getMessage());
        }
    }

    /**
     * 
     * @param t
     * @return
     */
    public long getSize(MimeMultipart t) {
        return -1;
    }

    /**
     * 
     * @param t
     * @param genericType
     * @param annotations
     * @param mediaType
     * @param httpHeaders
     * @param entityStream
     * @throws java.io.IOException
     */
    public void writeTo(MimeMultipart t, 
                        Type genericType,
                        Annotation[] annotations, 
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) 
                        throws IOException {
        try {
            t.writeTo(entityStream);
        } catch (MessagingException ex) {
            throw new IOException(""+ex.getMessage());
        }
    }
}
