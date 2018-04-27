package org.jboss.resteasy.test.stream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Stream;

import rx.Observable;

@Path("")
public class StreamRawObservableRxJava1Resource {

   @GET
   @Path("byte")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<Byte> aByte() {
      Byte[] array = new Byte[] {(byte) 0, (byte) 1, (byte) 2};
      return Observable.from(array);
   }

   @GET
   @Path("bytes")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<Byte[]> bytes() {
      Byte[] bytes = new Byte[] {0, 1, 2};
      return Observable.from(new Byte[][] {bytes, bytes, bytes});
   }


   @GET
   @Path("char")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character> aChar() {
      Character[] array = new Character[] {'a', 'b', 'c'};
      return Observable.from(array);
   }

   @GET
   @Path("chars")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character[]> chars() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.from(new Character[][] {chars, chars, chars});
   }
}
