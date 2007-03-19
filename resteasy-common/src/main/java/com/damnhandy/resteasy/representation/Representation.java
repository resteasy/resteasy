package com.damnhandy.resteasy.representation;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author ryan
 *
 * @param <T>
 */
public interface Representation<T> {

	/**
	 * @return the content
	 */
	public abstract T getContent();

	/**
	 * @return the eTag
	 */
	public abstract java.lang.String getETag();

	/**
	 * @return the lastModified
	 */
	public abstract java.util.GregorianCalendar getLastModified();

	/**
	 * @return the length
	 */
	public abstract long getLength();

	/**
	 * @return the mediaType
	 */
	public abstract java.lang.String getMediaType();

	/**
	 * 
	 * @param out
	 */
	public abstract void writeTo(OutputStream out) throws IOException;

}