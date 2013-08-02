package org.jboss.resteasy.examples.resteasy;

import org.jboss.resteasy.util.ReadFromStream;

import javax.swing.*;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("*/*")
public class ImageIconMessageBodyReader implements MessageBodyReader<ImageIcon>
{

	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType)
	{
		return type.isAssignableFrom(ImageIcon.class);
	}

	public ImageIcon readFrom(Class<ImageIcon> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException
	{
		return new ImageIcon(ReadFromStream.readFromStream(1024 * 4,
				entityStream));
	}

}
