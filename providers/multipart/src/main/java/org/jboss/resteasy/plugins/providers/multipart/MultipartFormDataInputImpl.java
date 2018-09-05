package org.jboss.resteasy.plugins.providers.multipart;

import org.apache.james.mime4j.field.ContentDispositionField;
import org.apache.james.mime4j.field.FieldName;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.parser.Field;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartFormDataInputImpl extends MultipartInputImpl implements
		MultipartFormDataInput {
	protected Map<String, List<InputPart>> formDataMap = new HashMap<String, List<InputPart>>();

	public MultipartFormDataInputImpl(MediaType contentType, Providers workers) {
		super(contentType, workers);
	}

	public Map<String, List<InputPart>> getFormDataMap() {
		return formDataMap;
	}

	public <T> T getFormDataPart(String key, Class<T> rawType, Type genericType)
			throws IOException {
		List<InputPart> list = getFormDataMap().get(key);
		if (list == null || list.isEmpty())
			return null;
		InputPart part = list.get(0);
		if (part == null)
			return null;
		return part.getBody(rawType, genericType);
	}

	public <T> T getFormDataPart(String key, GenericType<T> type)
			throws IOException {
		List<InputPart> list = getFormDataMap().get(key);
		if (list == null || list.isEmpty())
			return null;
		InputPart part = list.get(0);
		if (part == null)
			return null;
		return part.getBody(type);
	}

	@Override
	protected InputPart extractPart(BodyPart bodyPart) throws IOException {
		InputPart currPart = super.extractPart(bodyPart);
		Field disposition = bodyPart.getHeader().getField(
				FieldName.CONTENT_DISPOSITION);
		if (disposition == null)
		   throw new RuntimeException(Messages.MESSAGES.couldFindNoContentDispositionHeader());
		if (disposition instanceof ContentDispositionField) {
			String name = ((ContentDispositionField) disposition)
					.getParameter("name");
			List<InputPart> list = formDataMap.get(name);
			if (list == null) {
				list = new LinkedList<InputPart>();
				formDataMap.put(name, list);
			}
			list.add(currPart);
		} else {
		   throw new RuntimeException(Messages.MESSAGES.couldNotParseContentDisposition(disposition));
		}

		return currPart;
	}

   protected void finalize() throws Throwable
   {
      close();
   }
}
