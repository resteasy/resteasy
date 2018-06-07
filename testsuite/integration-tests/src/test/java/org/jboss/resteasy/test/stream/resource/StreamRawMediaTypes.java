package org.jboss.resteasy.test.stream.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;
import org.junit.Assert;

public class StreamRawMediaTypes {
   
   public static final MediaType applicationOctetStreamDefault;
   public static final MediaType applicationOctetStreamTrue;
   public static final MediaType textPlainDefault;
   public static final MediaType textPlainTrue;
   
   
   static {
      Map<String, String> map = new HashMap<String, String>();
      map.put("x", "y");
      applicationOctetStreamDefault = new MediaType("application", "octet-stream", map);
      
      map.put(Stream.INCLUDE_STREAMING_PARAMETER, "true");
      applicationOctetStreamTrue = new MediaType("application", "octet-stream", map);
      
      map.clear();
      map.put("charset", "UTF-8");
      textPlainDefault = new MediaType("text", "plain", map);
      
      map.put(Stream.INCLUDE_STREAMING_PARAMETER, "true");
      textPlainTrue = new MediaType("text", "plain", map);
   }
   
   public static void testMediaType(String type, String include, MediaType actual) {
      
      outer:
         switch (type) {
         
            case "byte":
               switch (include) {
                  case "default":
                  case "false":
                     Assert.assertEquals(StreamRawMediaTypes.applicationOctetStreamDefault, actual);
                     break outer;

                  case "true":
                     Assert.assertEquals(StreamRawMediaTypes.applicationOctetStreamTrue, actual);
                     break outer;
               }

            case "char":
               switch (include) {
                  case "default":
                  case "false":
                     Assert.assertEquals(StreamRawMediaTypes.textPlainDefault, actual);
                     break outer;

                  case "true":
                     Assert.assertEquals(StreamRawMediaTypes.textPlainTrue, actual);
                     break outer;
               }
         }
   }
}
