package org.jboss.resteasy.spi.interception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 */
@Deprecated
public interface MessageBodyReaderContext
{
   Class getType();

   void setType(Class type);

   Type getGenericType();

   void setGenericType(Type genericType);

   Annotation[] getAnnotations();

   void setAnnotations(Annotation[] annotations);

   MediaType getMediaType();

   void setMediaType(MediaType mediaType);

   MultivaluedMap<String, String> getHeaders();

   InputStream getInputStream();

   void setInputStream(InputStream is);

   /**
    * Allows you to pass values back and forth between interceptors
    * On the server side, this is the HttpRequest attributes, on the client side, this is the ClientRequest/ClientResponse
    * attributes.
    * @param attribute attribute name
    * @return attribute value
    */
   Object getAttribute(String attribute);

   void setAttribute(String name, Object value);

   void removeAttribute(String name);

   Object proceed() throws IOException, WebApplicationException;
}