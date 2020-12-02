package org.jboss.resteasy.test.providers.mediatype.resource;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.activation.DataSource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;

import org.jboss.resteasy.plugins.providers.FileRange;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.test.crypto.resource.CryptoCertResource;
import org.jboss.resteasy.test.providers.mediatype.BlacklistedMediaTypeTest;

@Path("")
public class BlacklistedMediaTypeResource {

   @GET
   @Path("byteArrayProvider")
   public byte[] byteArray() {
      return "hello".getBytes();
   }

   @GET
   @Path("dataSourceProvider")
   public DataSource dataSource() {
      return new DataSource() {
         @Override
         public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream("hello".getBytes());
         }

         @Override
         public OutputStream getOutputStream() throws IOException {
            throw new IOException();
         }

         @Override
         public String getContentType() {
            return "application/octet-stream";
         }

         @Override
         public String getName() {
            return "test";
         }
      };
   }

   @GET
   @Path("envelopedWriter")
   public EnvelopedOutput envelopedWriter() throws Exception {
      EnvelopedOutput eo = new EnvelopedOutput("hello", "text/plain");
      eo.setCertificate(CryptoCertResource.cert);
      return eo;
   }

   @GET
   @Path("fileProvider")
   public File fileProvider() throws Exception {
      String url = "src/test/resources/" +
            BlacklistedMediaTypeTest.class.getPackage().getName().replace('.', File.separatorChar) +
            "/BlacklistedMediaTypeFile1";
      File file = new File(url);
      return file;
   }

   @GET
   @Path("fileRangeWriter")
   public FileRange fileRangeWriter() throws Exception {
      String url = "src/test/resources/" +
            BlacklistedMediaTypeTest.class.getPackage().getName().replace('.', File.separatorChar) +
            "/BlacklistedMediaTypeFile2";
      File file = new File(url);
      FileRange fileRange = new FileRange(file, 2, 6);
      return fileRange;
   }

   @GET
   @Path("inputStreamProvider")
   public InputStream inputStreamProvider() throws Exception {
      ByteArrayInputStream baos = new ByteArrayInputStream("hello".getBytes());
      return baos;
   }

   @GET
   @Path("readerProvider")
   public Reader readerProvider() throws Exception {
      return new CharArrayReader("hello".toCharArray());
   }

   @GET
   @Path("streamingOutputProvider")
   public StreamingOutput streamingOutputProvider() throws Exception {
      return new StreamingOutput() {

         @Override
         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            outputStream.write("hello".getBytes());
         }
      };
   }

   @GET
   @Path("stringTextStar")
   public String str() {
      return "hello";
   }
}
