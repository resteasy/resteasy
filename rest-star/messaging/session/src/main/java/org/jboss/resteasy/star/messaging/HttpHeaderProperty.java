package org.jboss.resteasy.star.messaging;

public class HttpHeaderProperty
{
   public static final String CONTENT_TYPE = "http_content$type";

   /**
    * Converts an HTTP header name to a selector compatible property name.  '-' character is converted to
    * '$'. The return property name will also be all lower case with an "http_" prepended.  For example
    * "Content-Type" would be converted to "http_content$type";
    *
    * @param httpHeader
    * @return
    */
   public static String toPropertyName(String httpHeader)
   {
      httpHeader = httpHeader.replace('-', '$');
      return "http_" + httpHeader.toLowerCase();
   }

   /**
    * Converts a JMS property name to an HTTP header name.
    *
    * @param name
    * @return null if property name isn't an HTTP header name.
    */
   public static String fromPropertyName(String name)
   {
      if (!name.startsWith("http_")) return null;
      return name.substring("http_".length()).replace('$', '-');
   }
}