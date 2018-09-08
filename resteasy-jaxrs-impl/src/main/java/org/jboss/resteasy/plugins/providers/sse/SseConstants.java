package org.jboss.resteasy.plugins.providers.sse;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.GenericType;

public class SseConstants
{
   public static final String LAST_EVENT_ID_HEADER = "Last-Event-ID";

   public static final GenericType<String> STRING_AS_GENERIC_TYPE = new GenericType<>(String.class);

   public static final byte[] COMMENT_LEAD = ": ".getBytes(StandardCharsets.UTF_8);

   public static final byte[] NAME_LEAD = "event: ".getBytes(StandardCharsets.UTF_8);

   public static final byte[] ID_LEAD = "id: ".getBytes(StandardCharsets.UTF_8);

   public static final byte[] RETRY_LEAD = "retry: ".getBytes(StandardCharsets.UTF_8);

   public static final byte[] DATA_LEAD = "data: ".getBytes(StandardCharsets.UTF_8);

   public static final byte[] EOL = "\n".getBytes(StandardCharsets.UTF_8);

   //event delimiter can be '\r\r', '\n\n' or '\r\n\r\n'
   public static final byte[] EVENT_DELIMITER = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);

   public enum EVENT {
      START, COMMENT, FIELD,
   }
   
   public static final String SSE_ELEMENT_MEDIA_TYPE = "element-type";
}
