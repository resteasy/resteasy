package org.jboss.resteasy.test.charset;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

public class MediaTypes
{
   public static final MediaType APPLICATION_XML_UTF16_TYPE;
   public static final MediaType TEXT_PLAIN_UTF16_TYPE;
   public static final MediaType WILDCARD_UTF16_TYPE;
   public static final String APPLICATION_XML_UTF16 = "application/xml;charset=UTF-16";
   public static final String TEXT_PLAIN_UTF16 = "text/plain;charset=UTF-16";
   public static final String WILDCARD_UTF16 = "*/*;charset=UTF-16";
   
   static
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("charset", "UTF-16");
      APPLICATION_XML_UTF16_TYPE = new MediaType("application", "xml", params);
      TEXT_PLAIN_UTF16_TYPE = new MediaType("text", "plain", params);
      WILDCARD_UTF16_TYPE = new MediaType("*", "*", params);
   }
}
