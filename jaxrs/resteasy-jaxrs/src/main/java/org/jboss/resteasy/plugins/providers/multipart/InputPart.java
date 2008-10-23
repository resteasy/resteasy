package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InputPart
{
   MultivaluedMap<String, String> getHeaders();

   InputStream getBody();

   String getBodyAsString();

   <T> T getBody(Class<T> type, Type genericType) throws IOException;

   <T> T getBody(GenericType<T> type) throws IOException;

   MediaType getMediaType();
}
