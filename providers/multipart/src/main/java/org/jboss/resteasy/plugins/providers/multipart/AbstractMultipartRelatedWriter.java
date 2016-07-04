package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper base class for multipart/related producing providers.
 * 
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
public class AbstractMultipartRelatedWriter extends AbstractMultipartWriter {
	protected void writeRelated(MultipartRelatedOutput multipartRelatedOutput,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		for (OutputPart outputPart : multipartRelatedOutput.getParts())
			if (outputPart.getHeaders().get("Content-ID") == null)
				outputPart.getHeaders().putSingle("Content-ID",
						ContentIDUtils.generateContentID());
		OutputPart rootOutputPart = multipartRelatedOutput.getRootPart();
		Map<String, String> mediaTypeParameters = new LinkedHashMap<String, String>(
				mediaType.getParameters());
		if (mediaTypeParameters.containsKey("boundary"))
			multipartRelatedOutput.setBoundary(mediaTypeParameters
					.get("boundary"));
		mediaTypeParameters.put("start", (String) rootOutputPart.getHeaders()
				.getFirst("Content-ID"));
		mediaTypeParameters.put("type", rootOutputPart.getMediaType().getType()
				+ "/" + rootOutputPart.getMediaType().getSubtype());
		if (multipartRelatedOutput.getStartInfo() != null)
			mediaTypeParameters.put("start-info", multipartRelatedOutput
					.getStartInfo());
		MediaType modifiedMediaType = new MediaType(mediaType.getType(),
				mediaType.getSubtype(), mediaTypeParameters);
		write(multipartRelatedOutput, modifiedMediaType, httpHeaders,
				entityStream);
	}
}
