package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class EventByteArrayOutputStream extends ByteArrayOutputStream
{
   public synchronized byte[] getEventPayLoad()
   {
      // delimiter is \r or \n
      if (count >= 2 && this.buf[count - 2] == this.buf[count - 1])
      {
         return Arrays.copyOf(buf, count - 1);
      }
      //delimiter is \r\n
      if (count >= 2 && buf[count - 2] == '\r' && buf[count - 1] == '\n')
      {
         return Arrays.copyOf(buf, count - 2);
      }
      return Arrays.copyOf(buf, count);
   }
}
