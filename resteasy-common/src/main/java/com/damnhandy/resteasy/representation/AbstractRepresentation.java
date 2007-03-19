/**
 * 
 */
package com.damnhandy.resteasy.representation;

import com.damnhandy.resteasy.common.HttpHeaders;

/**
 * @author Ryan J. McDonough
 * 
 */
public abstract class AbstractRepresentation<T> implements Representation<T> {
	protected T content;

	protected java.lang.String eTag;

	protected java.util.GregorianCalendar lastModified;

	protected long length;

	protected java.lang.String mediaType;
	
	private HttpHeaders httpHeaders = new HttpHeaders();
	
	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#getContent()
	 */
	public T getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(T content) {
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#getETag()
	 */
	public java.lang.String getETag() {
		return eTag;
	}

	/**
	 * @param tag the eTag to set
	 */
	public void setETag(java.lang.String tag) {
		eTag = tag;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#getLastModified()
	 */
	public java.util.GregorianCalendar getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(java.util.GregorianCalendar lastModified) {
		this.lastModified = lastModified;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#getLength()
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.Representation#getMediaType()
	 */
	public java.lang.String getMediaType() {
		return mediaType;
	}

	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(java.lang.String mediaType) {
		this.mediaType = mediaType;
	}
}
