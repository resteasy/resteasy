/**
 * 
 */
package org.resteasy.plugins.providers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 23,
 *         2008
 * 
 */
public abstract class AbstractEntityProvider<T> implements
	MessageBodyReader<T>, MessageBodyWriter<T> {

    /**
     * 
     * @param in
     * @return
     * @throws IOException
     */
    protected String readString(InputStream in) throws IOException {
	char[] buffer = new char[100];
	StringBuilder builder = new StringBuilder();
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	int wasRead = 0;
	do {
	    wasRead = reader.read(buffer, 0, 100);
	    if (wasRead > 0) {
		builder.append(buffer, 0, wasRead);
	    }
	} while (wasRead > -1);

	return builder.toString();
    }
    
    /**
     * 
     * @param mediaTypes
     * @return
     */
    public List<MediaType> getAvailableMediaTypes(String[] mediaTypes) {
	List<MediaType> types = new ArrayList<MediaType>();
	for(String mediaType : mediaTypes) {
	    types.add(MediaType.valueOf(mediaType));
	}
	return types;
    }
    
    /**
     * 
     * @param mediaTypes
     * @return
     */
    public List<Variant> getAvailableVariants(String[] mediaTypes) {
	return getAvailableVariants(getAvailableMediaTypes(mediaTypes));
    }
    /**
     * 
     * @param mediaTypes
     * @return
     */
    public List<Variant> getAvailableVariants(List<MediaType> mediaTypes) {
	VariantListBuilder builder = Variant.VariantListBuilder.newInstance();
	MediaType[] types = mediaTypes.toArray(new MediaType[mediaTypes.size()]);
	builder.mediaTypes(types);
	return builder.build();
    }
    /**
     * 
     * @param in
     * @param mediaType
     * @return
     * @throws IOException
     */
    public DataSource readDataSource(InputStream in, MediaType mediaType) throws IOException {
	ByteArrayDataSource ds = 
	    new ByteArrayDataSource(
		    new BufferedInputStream(in), mediaType.toString());
	
	return ds;
    }
    
    /**
     * 
     */
    public long getSize(T t) {
	return -1;
    }
    
    /**
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public void writeTo(final InputStream in, 
	    	        final OutputStream out) throws IOException {
        int read;
        final byte[] buf = new byte[2048];
        while ((read = in.read(buf)) != -1) {
            out.write(buf, 0, read);
        }   
    }
}
