package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Variant.VariantListBuilder;

import org.jboss.resteasy.spi.AsyncOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A utility class to provide supporting functionality to various
 * entity providers.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: $
 */
public final class ProviderHelper
{

   private ProviderHelper()
   {

   }

   /**
    * @param in input stream
    * @return string data
    * @throws IOException if I/O error occurred
    */
   public static String readString(InputStream in) throws IOException
   {
      char[] buffer = new char[1024];
      StringBuilder builder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int wasRead = 0;
      do
      {
         wasRead = reader.read(buffer, 0, 1024);
         if (wasRead > 0)
         {
            builder.append(buffer, 0, wasRead);
         }
      }
      while (wasRead > -1);

      return builder.toString();
   }

   /**
    * @param in input stream
    * @param mediaType media type
    * @return string data
    * @throws IOException if I/O error occurred
    */
   public static String readString(InputStream in, MediaType mediaType) throws IOException
   {
      byte[] buffer = new byte[1024];
      ByteArrayOutputStream builder = new ByteArrayOutputStream();
      int wasRead = 0;
      do
      {
         wasRead = in.read(buffer, 0, 1024);
         if (wasRead > 0)
         {
            builder.write(buffer, 0, wasRead);
         }
      }
      while (wasRead > -1);
      byte[] bytes = builder.toByteArray();

      String charset = mediaType.getParameters().get("charset");
      if (charset != null) return new String(bytes, charset);
      else return new String(bytes, StandardCharsets.UTF_8);
   }

   /**
    * @param mediaTypes string array of media types
    * @return list of media types
    */
   public static List<MediaType> getAvailableMediaTypes(String[] mediaTypes)
   {
      List<MediaType> types = new ArrayList<MediaType>();
      for (String mediaType : mediaTypes)
      {
         types.add(MediaType.valueOf(mediaType));
      }
      return types;
   }

   /**
    * @param mediaTypes string array of media types
    * @return list of {@link Variant}
    */
   public static List<Variant> getAvailableVariants(String[] mediaTypes)
   {
      return getAvailableVariants(getAvailableMediaTypes(mediaTypes));
   }

   /**
    * @param mediaTypes list of media types
    * @return list of {@link Variant}
    */
   public static List<Variant> getAvailableVariants(List<MediaType> mediaTypes)
   {
      VariantListBuilder builder = Variant.VariantListBuilder.newInstance();
      MediaType[] types = mediaTypes.toArray(new MediaType[mediaTypes.size()]);
      builder.mediaTypes(types);
      return builder.build();
   }

   /**
    * @param in input stream
    * @param out output stream
    * @throws IOException if I/O error occurred
    */
   public static void writeTo(final InputStream in, final OutputStream out) throws IOException
   {
      int read;
      final byte[] buf = new byte[2048];
      while ((read = in.read(buf)) != -1)
      {
         out.write(buf, 0, read);
      }
   }

   /**
    * @param in input stream
    * @param out output stream
    * @throws IOException if I/O error occurred
    */
   public static CompletionStage<Void> writeToAndCloseInput(final InputStream in, final AsyncOutputStream out)
   {
      return writeTo(in, out).whenComplete((v, t) -> {
         try {
            in.close();
         } catch(IOException x) {
            throw new RuntimeException(x);
         }
      });
   }

   /**
    * @param in input stream
    * @param out output stream
    * @throws IOException if I/O error occurred
    */
   public static CompletionStage<Void> writeTo(final InputStream in, final AsyncOutputStream out)
   {
      final byte[] buf = new byte[2048];
      return writeTo(in, out, buf);
   }

   private static CompletionStage<Void> writeTo(final InputStream in, final AsyncOutputStream out, byte[] buf)
   {
      int read;
      try
      {
         if ((read = in.read(buf)) != -1)
         {
            return out.asyncWrite(buf, 0, read)
                  .thenCompose(v -> writeTo(in, out, buf));
         }
      } catch (IOException e)
      {
         return completedException(e);
      }
      return CompletableFuture.completedFuture(null);
   }

   public static CompletionStage<Void> completedException(Throwable t)
   {
      CompletableFuture<Void> ret = new CompletableFuture<>();
      ret.completeExceptionally(t);
      return ret;
   }
}
