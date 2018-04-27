package org.jboss.resteasy.test.stream.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.sse.SseConstants;

public class StreamRawMediaTypes {

   public static final String rawStreamApplicationOctetXY;
   public static final String rawStreamTextPlainUTF8;
   
   static {
      Map<String, String> map = new HashMap<String, String>();
      map.put(SseConstants.SSE_ELEMENT_MEDIA_TYPE, "application/octet-stream;x=y");
      rawStreamApplicationOctetXY = new MediaType("application", "x-stream-raw", map).toString();
      map.clear();
      map.put(SseConstants.SSE_ELEMENT_MEDIA_TYPE, "text/plain;charset=UTF-8");
      rawStreamTextPlainUTF8 = new MediaType("application", "x-stream-raw", map).toString();
   }
   
}
