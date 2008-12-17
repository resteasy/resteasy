package org.jboss.resteasy.spi;

import java.net.URI;

public class ObjectFor
{

   public Object objectFor(URI uri)
   {
      if (uri.isAbsolute())
      {
         throw new RuntimeException("A uri passed to ObjectFor needs to be relative");
      }
      return null;
   }
}
