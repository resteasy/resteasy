package org.jboss.resteasy.plugins.providers.sse;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class EventByteArrayOutputStream extends ByteArrayOutputStream
{

   private void removeBlankLine()
   {
      if (this.count > 4)
      {
         count = count - 4;
      }
   }

   public synchronized byte getEventPayLoad()[]
   {
      removeBlankLine();
      return Arrays.copyOf(buf, count);
   }
}
