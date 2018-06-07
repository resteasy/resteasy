package org.jboss.resteasy.test.stream.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Stream;

import io.reactivex.Flowable;

@Path("")
public class StreamRawFlowableRxJava2Resource {

   @GET
   @Path("byte/default")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Flowable<Byte> aByteDefault() {
      return Flowable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }
   
   @GET
   @Path("byte/false")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Flowable<Byte> aByteFalse() {
      return Flowable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }
   
   @GET
   @Path("byte/true")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Flowable<Byte> aByteTrue() {
      return Flowable.fromArray((byte) 0, (byte) 1, (byte) 2);
   }

   @GET
   @Path("bytes/default")
   @Produces("application/octet-stream;x=y")
   @Stream(Stream.MODE.RAW)
   public Flowable<byte[]> bytesDefault() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Flowable.fromArray(bytes, bytes, bytes);
   }

   @GET
   @Path("bytes/false")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Flowable<byte[]> bytesFalse() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Flowable.fromArray(bytes, bytes, bytes);
   }
   
   @GET
   @Path("bytes/true")
   @Produces("application/octet-stream;x=y")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Flowable<byte[]> bytesTrue() {
      byte[] bytes = new byte[] {0, 1, 2};
      return Flowable.fromArray(bytes, bytes, bytes);
   }
   
   @GET
   @Path("char/default")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Flowable<Character> aCharDefault() {
      return Flowable.fromArray('a', 'b', 'c');
   }
   
   @GET
   @Path("char/false")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Flowable<Character> aCharFalse() {
      return Flowable.fromArray('a', 'b', 'c');
   }
   
   @GET
   @Path("char/true")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Flowable<Character> aCharTrue() {
      return Flowable.fromArray('a', 'b', 'c');
   }

   @GET
   @Path("chars/default")
   @Produces("text/plain;charset=UTF-8")
   @Stream(Stream.MODE.RAW)
   public Flowable<Character[]> charsDefault() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Flowable.fromArray(chars, chars, chars);
   }
   
   @GET
   @Path("chars/false")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=false)
   public Flowable<Character[]> charsFalse() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Flowable.fromArray(chars, chars, chars);
   }
   
   @GET
   @Path("chars/true")
   @Produces("text/plain;charset=UTF-8")
   @Stream(value=Stream.MODE.RAW, includeStreaming=true)
   public Flowable<Character[]> charsTrue() {
      Character[] chars = new Character[] {'a', 'b', 'c'};
      return Flowable.fromArray(chars, chars, chars);
   }
}
