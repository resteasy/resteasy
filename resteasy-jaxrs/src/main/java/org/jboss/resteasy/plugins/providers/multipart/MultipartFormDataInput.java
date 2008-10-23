package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.util.GenericType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MultipartFormDataInput extends MultipartInput
{
   Map<String, InputPart> getFormData();

   <T> T getFormDataPart(String key, Class<T> rawType, Type genericType) throws IOException;

   <T> T getFormDataPart(String key, GenericType<T> type) throws IOException;
}
