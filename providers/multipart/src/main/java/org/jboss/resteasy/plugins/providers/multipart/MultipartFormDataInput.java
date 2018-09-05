package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MultipartFormDataInput extends MultipartInput {
	/**
	 * @return A parameter map containing a list of values per name.
	 */
	Map<String, List<InputPart>> getFormDataMap();

	<T> T getFormDataPart(String key, Class<T> rawType, Type genericType)
			throws IOException;

	<T> T getFormDataPart(String key, GenericType<T> type) throws IOException;
}
