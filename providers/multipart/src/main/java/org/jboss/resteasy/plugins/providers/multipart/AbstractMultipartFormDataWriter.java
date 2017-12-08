package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractMultipartFormDataWriter extends AbstractMultipartWriter {
	@Override
	protected void writeParts(MultipartOutput multipartOutput,
									  OutputStream entityStream, byte[] boundaryBytes) throws IOException {
		if (!(multipartOutput instanceof MultipartFormDataOutput))
			throw new IllegalArgumentException(Messages.MESSAGES.hadToWriteMultipartOutput(multipartOutput, this, MultipartFormDataOutput.class));
		MultipartFormDataOutput form = (MultipartFormDataOutput) multipartOutput;
		for (Map.Entry<String, List<OutputPart>> entry : form.getFormDataMap().entrySet()) {
			for (OutputPart outputPart : entry.getValue()) {
				if (outputPart.getEntity() == null) {
					continue;
				}
				MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
				headers.putSingle("Content-Disposition", "form-data; name=\""
						+ entry.getKey() + "\""
						+ getFilename(outputPart));
				writePart(entityStream, boundaryBytes, outputPart, headers);
			}
		}
	}

	private String getFilename(OutputPart part) {
		String filename = part.getFilename(); 
		if (filename == null) {
			return "";
		} else {
			return "; filename=\"" + filename + "\"";
		}
	}	
}
