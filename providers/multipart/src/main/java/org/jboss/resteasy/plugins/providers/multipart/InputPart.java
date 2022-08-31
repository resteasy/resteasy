package org.jboss.resteasy.plugins.providers.multipart;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * Represents one part of a multipart message.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InputPart {
   /**
    * If no content-type header is sent in a multipart message part
    * "text/plain; charset=ISO-8859-1" is assumed.
    * <p>
    * This can be overwritten by setting a different String value in
    * {@link org.jboss.resteasy.spi.HttpRequest#setAttribute(String, Object)}
    * with this ("resteasy.provider.multipart.inputpart.defaultContentType")
    * String as key. It should be done in a
    * {@link jakarta.ws.rs.container.ContainerRequestFilter}.
    * </p>
    */
   String DEFAULT_CONTENT_TYPE_PROPERTY = "resteasy.provider.multipart.inputpart.defaultContentType";

   /**
    * If there is a content-type header without a charset parameter, charset=US-ASCII
    * is assumed.
    * <p>
    * This can be overwritten by setting a different String value in
    * {@link org.jboss.resteasy.spi.HttpRequest#setAttribute(String, Object)}
    * with this ("resteasy.provider.multipart.inputpart.defaultCharset")
    * String as key. It should be done in a
    * {@link jakarta.ws.rs.container.ContainerRequestFilter}.
    * </p>
    */
   String DEFAULT_CHARSET_PROPERTY = "resteasy.provider.multipart.inputpart.defaultCharset";

   /**
    * @return headers of this part
    */
   MultivaluedMap<String, String> getHeaders();

   String getBodyAsString() throws IOException;

   <T> T getBody(Class<T> type, Type genericType) throws IOException;

   <T> T getBody(GenericType<T> type) throws IOException;

   /**
    * Returns the body content as an input stream.
    *
    * @return the body content
    *
    * @throws IOException if an error occurs reading the content
    */
   default InputStream getBody() throws IOException {
      final String value = getBodyAsString();
      if (value == null) {
         return InputStream.nullInputStream();
      }
      return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
   }

   /**
    * Returns the filename parameter of the content disposition of this Entity.
    *
    * @return the filename or {@code null} if one does not exist
    */
   default String getFileName() {
      return null;
   }

   /**
    * @return "Content-Type" of this part
    */
   MediaType getMediaType();

   /**
    * @return true if the Content-Type was resolved from the message, false if
    *         it was resolved from the server default
    */
   boolean isContentTypeFromMessage();

   /**
    * Change the media type of the body part before you extract it.  Useful for specifying a charset.
    *
    * @param mediaType media type
    */
   void setMediaType(MediaType mediaType);
}
