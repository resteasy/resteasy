package org.jboss.resteasy.test.stream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;

import io.reactivex.Observable;

@Path("")
public class StreamRawObservableRxJava2Resource {

   @GET
   @Path("byte")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<Byte> aByte() {
      return Observable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }

   @GET
   @Path("bytes")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<byte[]> bytes() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Observable.fromArray(bytes, bytes, bytes);
   }


   @GET
   @Path("char")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character> aChar() {
      return Observable.fromArray('a', 'b', 'c');
   }

   @GET
   @Path("chars")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character[]> chars() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.fromArray(chars, chars, chars);
   }
}
