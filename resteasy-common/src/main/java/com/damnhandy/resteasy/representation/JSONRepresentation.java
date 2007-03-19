/**
 * 
 */
package com.damnhandy.resteasy.representation;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.sojo.interchange.AbstractSerializer;
import net.sf.sojo.interchange.json.JsonSerializer;

import com.damnhandy.resteasy.exceptions.RespresentationHandlerException;

/**
 * @author Ryan J. McDonough
 * @since 1.0
 *
 */
public class JSONRepresentation<T> extends AbstractRepresentation<T> {

	/**
	 * Date format needed by SOJO to desrialize a date
	 */
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private AbstractSerializer serializer = new JsonSerializer();
	private String jsonContent;
	/**
	 * 
	 * @param content
	 */
	public JSONRepresentation(T content) {
		this.setContent(content);
		this.setMediaType("application/json");
		createJSONContent();
		this.setLength(jsonContent.getBytes().length);
	}
	
	/**
	 * 
	 *
	 */
	private void createJSONContent() {
		serializer.getObjectUtil().addFormatterForType(DEFAULT_DATE_FORMAT, Date.class);
		jsonContent = (String) serializer.serialize(getContent());
	}
	
	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream out) throws IOException {
		try {
			out.write(jsonContent.getBytes());
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}

	}

}
