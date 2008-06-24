/**
 * 
 */
package org.resteasy.plugins.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.activation.DataSource;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * Jun 23, 2008
 *
 */
@Provider
@ConsumeMime("*/*")
@ProduceMime("*/*")
public class DataSourceProvider extends AbstractEntityProvider<DataSource> {

    /* (non-Javadoc)
     * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[])
     */
    public boolean isReadable(Class<?> type, Type genericType,
	    Annotation[] annotations) {
	return DataSource.class.isAssignableFrom(type);
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
     */
    public DataSource readFrom(Class<DataSource> type, Type genericType,
	    Annotation[] annotations, MediaType mediaType,
	    MultivaluedMap<String,String> httpHeaders, InputStream entityStream)
	    throws IOException, WebApplicationException {
	
	return readDataSource(entityStream, mediaType);
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[])
     */
    public boolean isWriteable(Class<?> type, Type genericType,
	    Annotation[] annotations) {
	return DataSource.class.isAssignableFrom(type);
    }

    

    public void writeTo(DataSource dataSource, 
	    		Class<?> type, 
	    		Type genericType,
	    		Annotation[] annotations, 
	    		MediaType mediaType,
	    		MultivaluedMap<String, Object> httpHeaders,
	    		OutputStream entityStream) throws IOException,
	    WebApplicationException {
	InputStream in = dataSource.getInputStream();
	try {
	    writeTo(in, entityStream);
	} finally {
	    in.close();
	}
	
    }
    
    

}
