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
public class PlainTextRepresentation extends AbstractRepresentation<String> {

	/**
	 * 
	 * @param content
	 */
	public PlainTextRepresentation(String content) {
		this.setContent(content);
		this.setLength(getContent().getBytes().length);
		this.setMediaType("text/plain");
	}
	
	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream out) throws IOException {
		// TODO Auto-generated method stub

	}

}
