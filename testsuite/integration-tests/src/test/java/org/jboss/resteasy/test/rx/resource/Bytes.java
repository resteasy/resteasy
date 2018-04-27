package org.jboss.resteasy.test.rx.resource;

public class Bytes {
   public static final byte[] BYTES = new byte[256];

   static {
      for (int i = 0; i < 256; i++) {BYTES[i] = (byte) i;}
   }
}
