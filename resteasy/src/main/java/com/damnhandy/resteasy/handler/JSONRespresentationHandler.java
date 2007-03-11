/**
 * 
 */
package com.damnhandy.resteasy.handler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.sojo.interchange.AbstractSerializer;
import net.sf.sojo.interchange.json.JsonSerializer;

import com.damnhandy.resteasy.RespresentationHandlerException;

/**
 * A representation handler capable of reading and writing JSON objects.
 * 
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Mar 9, 2007
 * 
 */
public class JSONRespresentationHandler implements RepresentationHandler {

	/**
	 * 
	 */
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/** 
	 * Deserializes an input stream containing a JSON string into the specified Java type.
	 * 
	 * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleRequest(java.io.InputStream, java.lang.Class)
	 */
	public Object handleRequest(InputStream in, Class clazz)
			throws RespresentationHandlerException {
		Object result = null;
		try {
			AbstractSerializer serializer = new JsonSerializer();
			serializer.getObjectUtil().addFormatterForType(DEFAULT_DATE_FORMAT, Date.class);
			StringBuilder b = new StringBuilder();
			BufferedInputStream buf = new BufferedInputStream(in);
			int c;
			while ((c = buf.read()) != -1) {
				b.append((char) c);
			}
			result = serializer.deserialize(b.toString(),clazz);
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}
		return result;
	}

	/** 
	 * Serialized the Java to a JSON object.
	 * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleResponse(java.io.OutputStream, java.lang.Object)
	 */
	public void handleResponse(OutputStream out, Object result)
			throws RespresentationHandlerException {
		try {
			AbstractSerializer serializer = new JsonSerializer();
			serializer.getObjectUtil().addFormatterForType(DEFAULT_DATE_FORMAT, Date.class);
			String jsonString = (String) serializer.serialize(result);
			out.write(jsonString.getBytes());
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}

	}

}
