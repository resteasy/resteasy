package org.jboss.resteasy.test.stream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Stream;

import rx.Observable;

@Path("")
public class StreamRawObservableRxJava1Resource {

   @GET
   @Path("byte/default")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<Byte> aByteDefault() {
      Byte[] array = new Byte[] {(byte) 0, (byte) 1, (byte) 2};
      return Observable.from(array);
   }
   
   @GET
   @Path("byte/false")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Byte> aByteFalse() {
      Byte[] array = new Byte[] {(byte) 0, (byte) 1, (byte) 2};
      return Observable.from(array);
   }
   
   @GET
   @Path("byte/true")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Byte> aByteTrue() {
      Byte[] array = new Byte[] {(byte) 0, (byte) 1, (byte) 2};
      return Observable.from(array);
   }

   @GET
   @Path("bytes/default")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Observable<Byte[]> bytesDefault() {
      Byte[] bytes = new Byte[] {0, 1, 2};
      return Observable.from(new Byte[][] {bytes, bytes, bytes});
   }

   @GET
   @Path("bytes/false")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Byte[]> bytesFalse() {
      Byte[] bytes = new Byte[] {0, 1, 2};
      return Observable.from(new Byte[][] {bytes, bytes, bytes});
   }
   
   @GET
   @Path("bytes/true")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Byte[]> bytesTrue() {
      Byte[] bytes = new Byte[] {0, 1, 2};
      return Observable.from(new Byte[][] {bytes, bytes, bytes});
   }
   
   @GET
   @Path("char/default")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character> aCharDefault() {
      Character[] array = new Character[] {'a', 'b', 'c'};
      return Observable.from(array);
   }
   
   @GET
   @Path("char/false")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Character> aCharFalse() {
      Character[] array = new Character[] {'a', 'b', 'c'};
      return Observable.from(array);
   }
   
   @GET
   @Path("char/true")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Character> aCharTrue() {
      Character[] array = new Character[] {'a', 'b', 'c'};
      return Observable.from(array);
   }

   @GET
   @Path("chars/default")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Observable<Character[]> charsDefault() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.from(new Character[][] {chars, chars, chars});
   }
   
   @GET
   @Path("chars/false")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Observable<Character[]> charsFalse() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.from(new Character[][] {chars, chars, chars});
   }
   
   @GET
   @Path("chars/true")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Observable<Character[]> charsTrue() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Observable.from(new Character[][] {chars, chars, chars});
   }
}
