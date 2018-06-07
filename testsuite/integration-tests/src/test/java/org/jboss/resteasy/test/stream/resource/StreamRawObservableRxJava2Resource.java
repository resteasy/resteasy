package org.jboss.resteasy.test.stream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Stream;

import io.reactivex.Observable;

@Path("")
public class StreamRawObservableRxJava2Resource {

   @GET
   @Path("byte/default")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<Byte> aByteDefault() {
      return Observable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }
   
   @GET
   @Path("byte/false")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Byte> aByteFalse() {
      return Observable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }
   
   @GET
   @Path("byte/true")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Byte> aByteTrue() {
      return Observable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }

   @GET
   @Path("bytes/default")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<byte[]> bytesDefault() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Observable.fromArray(bytes, bytes, bytes);
   }

   @GET
   @Path("bytes/false")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<byte[]> bytesFalse() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Observable.fromArray(bytes, bytes, bytes);
   }
   @GET
   @Path("bytes/true")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<byte[]> bytesTrue() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Observable.fromArray(bytes, bytes, bytes);
   }

   @GET
   @Path("char/default")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character> aCharDefault() {
      return Observable.fromArray('a', 'b', 'c');
   }
   
   @GET
   @Path("char/false")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Character> aCharFalse() {
      return Observable.fromArray('a', 'b', 'c');
   }
   
   @GET
   @Path("char/true")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Character> aCharTrue() {
      return Observable.fromArray('a', 'b', 'c');
   }

   @GET
   @Path("chars/default")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character[]> charsDefault() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.fromArray(chars, chars, chars);
   }
   
   @GET
   @Path("chars/false")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Character[]> charsFalse() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.fromArray(chars, chars, chars);
   }
   
   @GET
   @Path("chars/true")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Character[]> charsTrue() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.fromArray(chars, chars, chars);
   }
}
