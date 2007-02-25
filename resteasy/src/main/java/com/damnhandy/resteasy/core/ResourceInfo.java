package com.damnhandy.resteasy.core;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * @author Ryan J. McDonough Feb 16, 2007
 * 
 */
public class ResourceInfo {

	/**
	 * HTTP date format.
	 */
	protected static final SimpleDateFormat format = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	/**
	 * Date formats using for Date parsing.
	 */
	protected static final SimpleDateFormat formats[] = {
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
			new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US),
			new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
			new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") };

	/**
	 * Simple date format for the creation date ISO representation (partial).
	 */
	protected static final SimpleDateFormat creationDateFormat = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		creationDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		for (int i = 0; i < formats.length; i++) {
			formats[i].setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	}
}
