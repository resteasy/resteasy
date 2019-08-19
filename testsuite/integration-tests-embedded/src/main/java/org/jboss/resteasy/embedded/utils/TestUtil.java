package org.jboss.resteasy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Base util class for Embedded server testing.
 */
public class TestUtil {


   /**
    * Convert input stream to String.
    *
    * @param in Input stream
    * @return Converted string
    */
   public static String readString(final InputStream in) throws IOException {
      char[] buffer = new char[1024];
      StringBuilder builder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int wasRead = 0;
      do {
         wasRead = reader.read(buffer, 0, 1024);
         if (wasRead > 0) {
            builder.append(buffer, 0, wasRead);
         }
      }
      while (wasRead > -1);

      return builder.toString();
   }

}
