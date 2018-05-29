package org.jboss.resteasy.test.stream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Stream;

import io.reactivex.Flowable;

@Path("")
public class StreamRawFlowableRxJava2Resource {

   @GET
   @Path("byte")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Flowable<Byte> aByte() {
      return Flowable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }

   @GET
   @Path("bytes")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Flowable<byte[]> bytes() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Flowable.fromArray(bytes, bytes, bytes);
   }


   @GET
   @Path("char")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Flowable<Character> aChar() {
      return Flowable.fromArray('a', 'b', 'c');
   }

   @GET
   @Path("chars")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Flowable<Character[]> chars() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Flowable.fromArray(chars, chars, chars);
   }
}
