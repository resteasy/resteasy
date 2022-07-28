package org.jboss.resteasy.grpc.runtime.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility for parsing and generating dates
 *
 * @author Stuart Douglas (from undertow)
 */
public class DateUtils {

   private static final Locale LOCALE_US = Locale.US;

   private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

   private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

   /**
    * Thread local cache of this date format. This is technically a small memory leak, however
    * in practice it is fine, as it will only be used by server threads.
    * <p>
    * This is the most common date format, which is why we cache it.
    */
   private static final ThreadLocal<SimpleDateFormat> RFC1123_PATTERN_FORMAT = new ThreadLocal<SimpleDateFormat>() {
      @Override
      protected SimpleDateFormat initialValue() {
         SimpleDateFormat df = new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);
         return df;
      }
   };

   private static final String RFC1036_PATTERN = "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

   private static final String ASCITIME_PATTERN = "EEE MMM d HH:mm:ss yyyyy";

   private static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

   /**
    * Attempts to pass a HTTP date.
    *
    * @param date The date to parse
    * @return The parsed date, or null if parsing failed
    */
   public static Date parseDate(final String date) {

      /*
            IE9 sends a superflous lenght parameter after date in the
            If-Modified-Since header, which needs to be stripped before
            parsing.

       */

      final int semicolonIndex = date.indexOf(';');
      final String trimmedDate = semicolonIndex >= 0 ? date.substring(0, semicolonIndex) : date;

      ParsePosition pp = new ParsePosition(0);
      SimpleDateFormat dateFormat = RFC1123_PATTERN_FORMAT.get();
      dateFormat.setTimeZone(GMT_ZONE);
      Date val = dateFormat.parse(trimmedDate, pp);
      if (val != null && pp.getIndex() == trimmedDate.length()) {
         return val;
      }

      pp = new ParsePosition(0);
      dateFormat = new SimpleDateFormat(RFC1036_PATTERN, LOCALE_US);
      dateFormat.setTimeZone(GMT_ZONE);
      val = dateFormat.parse(trimmedDate, pp);
      if (val != null && pp.getIndex() == trimmedDate.length()) {
         return val;
      }

      pp = new ParsePosition(0);
      dateFormat = new SimpleDateFormat(ASCITIME_PATTERN, LOCALE_US);
      dateFormat.setTimeZone(GMT_ZONE);
      val = dateFormat.parse(trimmedDate, pp);
      if (val != null && pp.getIndex() == trimmedDate.length()) {
         return val;
      }

      pp = new ParsePosition(0);
      dateFormat = new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);
      dateFormat.setTimeZone(GMT_ZONE);
      val = dateFormat.parse(trimmedDate, pp);
      if (val != null && pp.getIndex() == trimmedDate.length()) {
         return val;
      }

      return null;
   }
}
