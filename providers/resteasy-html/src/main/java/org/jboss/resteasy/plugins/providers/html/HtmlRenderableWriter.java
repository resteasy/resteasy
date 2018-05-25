package org.jboss.resteasy.plugins.providers.html;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.spi.ResteasyProviderFactory.getContextData;

/**
 * 
 * @author <a href="mailto:jeff@infohazard.org">Jeff Schnitzer</a>
 * @author Thomas Broyer
 */
@Provider
@Produces("text/html")
public class HtmlRenderableWriter implements MessageBodyWriter<Renderable>
{

	/* (non-Javadoc
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	//@Override
	public long getSize(Renderable obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		// No chance of figuring this out ahead of time
		return -1;
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	//@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return Renderable.class.isAssignableFrom(type);
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	//@Override
	public void writeTo(Renderable viewingPleasure, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		try
		{
	      viewingPleasure.render(
	            // disable async processing as that would mess with interceptors,
	            // and entityStream is committed after writeTo and interceptors returns.
	            new HttpServletRequestWrapper(getContextData(HttpServletRequest.class)) {
	               
	               @Override
	               public boolean isAsyncSupported() {
	                  return false;
	               }

	               @Override
	               public boolean isAsyncStarted() {
	                  return false;
	               }

	               @Override
	               public AsyncContext getAsyncContext() {
	                  throw new IllegalStateException();
	               }

	               @Override
	               public AsyncContext startAsync() throws IllegalStateException {
	                  throw new IllegalStateException();
	               }

	               @Override
	               public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
	                  throw new IllegalStateException();
	               }
	            },
	            
	            // RESTEASY-1422: wrap entityStream to make sure headers added through JAX-RS are committed when the ServletOutputStream is written to.
	            // Also disable async processing on the ServletOutputStream, for consistency with the request.
	            new HttpServletResponseWrapper(getContextData(HttpServletResponse.class)) {
	               
	               private ServletOutputStream outputStream;
	               private PrintWriter writer;

	               @Override
	               public ServletOutputStream getOutputStream() {
	                  
	                  if (writer != null) {
	                     throw new IllegalStateException();
	                  }
	                  if (outputStream == null) {
	                     outputStream = new ServletOutputStream() {
	                        
	                        @Override
	                        public boolean isReady() {
	                           return true;
	                        }

	                        @Override
	                        public void setWriteListener(WriteListener writeListener) {
	                           throw new IllegalStateException();
	                        }

	                        @Override
	                        public void write(int b) throws IOException {
	                           entityStream.write(b);
	                        }

	                        @Override
	                        public void write(byte[] b) throws IOException {
	                           entityStream.write(b);
	                        }

	                        @Override
	                        public void write(byte[] b, int off, int len) throws IOException {
	                           entityStream.write(b, off, len);
	                        }

	                        @Override
	                        public void flush() throws IOException {
	                           entityStream.flush();
	                        }

	                        @Override
	                        public void close() throws IOException {
	                           entityStream.close();
	                        }
	                     };
	                  }
	                  return outputStream;
	               }

	               @Override
	               public PrintWriter getWriter() throws IOException {
	                  if (outputStream != null) {
	                     throw new IllegalStateException();
	                  }
	                  if (writer == null) {
	                     writer = new PrintWriter(new OutputStreamWriter(entityStream, getCharacterEncoding()));
	                  }
	                  return writer;
	               }
	            });
		}
		catch (ServletException ex)
		{
			throw new WebApplicationException(ex);
		}
	}
}
