/**
 * 
 */
package com.damnhandy.resteasy.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.damnhandy.resteasy.common.HttpHeaderNames;
import com.damnhandy.resteasy.common.HttpHeaders;
import com.damnhandy.resteasy.exceptions.RespresentationHandlerException;
import com.damnhandy.resteasy.representation.ByteArrayRepresentation;
import com.damnhandy.resteasy.representation.Representation;

/**
 * @author ryan
 *
 */
@MediaTypes(types={
		@MediaType(type="application/octet-stream",extentions="*")
})
public class ByteArrayRepresentationHandler implements RepresentationHandler<byte[]> {

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleRequest(java.io.InputStream, java.lang.Class)
	 */
	public byte[] handleRequest(InputStream in, Class<byte[]> c)
			throws RespresentationHandlerException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int counter;
		try {
			while ((counter = in.read()) != -1) {
				out.write(counter);
			}
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}
		return out.toByteArray();
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleResponse(java.io.OutputStream, java.lang.Object)
	 */
	public void handleResponse(OutputStream out, byte[] result)
			throws RespresentationHandlerException {
		try {
			out.write(result);
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}
	}

	/**
	 * 
	 */
	public Representation<byte[]> handleRequest(InputStream in, Class<byte[]> c, HttpHeaders headers) 
		throws RespresentationHandlerException {
		Representation<byte[]> representation = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int counter;
		try {
			while ((counter = in.read()) != -1) {
				out.write(counter);
			}
			String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE, String.class);
			representation = new ByteArrayRepresentation(out.toByteArray(),contentType);
			out.close();
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}
		return representation;
	}

}
