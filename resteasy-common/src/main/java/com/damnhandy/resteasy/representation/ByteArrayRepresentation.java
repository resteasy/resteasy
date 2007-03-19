/**
 * 
 */
package com.damnhandy.resteasy.representation;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author ryan
 *
 */
public class ByteArrayRepresentation extends AbstractRepresentation<byte[]> {
	
	/**
	 * @param data
	 */
	public ByteArrayRepresentation(byte[] content) {
		this.setContent(content);
		this.setLength(getContent().length);
		this.setMediaType("application/octet-stream");
	}
	/**
	 * @param data
	 * @param mediaType
	 */
	public ByteArrayRepresentation(byte[] data, String mediaType) {
		this(data);
		this.setMediaType(mediaType);
	}
	

	/**
	 * 
	 */
	public void writeTo(OutputStream out) throws IOException {
		out.write(this.getContent());
	}
	

}
