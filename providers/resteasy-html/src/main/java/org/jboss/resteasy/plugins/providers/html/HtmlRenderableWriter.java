package org.jboss.resteasy.plugins.providers.html;

import static org.jboss.resteasy.spi.ResteasyProviderFactory.getContextData;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@SuppressWarnings("unchecked")
@Provider
@Produces("text/html")
public class HtmlRenderableWriter implements MessageBodyWriter<Renderable>
{

	/* (non-Javadoc
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	//@Override
	public long getSize(Renderable obj, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		// No chance of figuring this out ahead of time
		return -1;
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	//@Override
	public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return Renderable.class.isAssignableFrom(type);
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	//@Override
	public void writeTo(Renderable viewingPleasure, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		try
		{
			viewingPleasure.render(getContextData(HttpServletRequest.class), getContextData(HttpServletResponse.class));
		}
		catch (ServletException ex)
		{
			throw new WebApplicationException(ex);
		}
	}
}
