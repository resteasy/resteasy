package org.jboss.resteasy.test.context.resource;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

import org.infinispan.commons.dataconversion.MediaType;

@Path("/")
public class HttpServletResponseContextResource {

   @Path("print/string")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printString(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print("context");
      return;
   }

   @Path("print/boolean")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printBoolean(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print(true);
      return;
   }

   @Path("print/char")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printChar(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print('c');
      return;
   }

   @Path("print/int")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printInt(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print(17);
      return;
   }

   @Path("print/long")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printLong(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print(17L);
      return;
   }

   @Path("print/float")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printFloat(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print(17.0F);
      return;
   }

   @Path("print/double")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printDouble(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.print(17D);
      return;
   }

   @Path("println/eol")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void println(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println();
      return;
   }

   @Path("println/string")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnString(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println("context");
      return;
   }

   @Path("println/boolean")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnBoolean(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println(true);
      return;
   }

   @Path("println/char")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnChar(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println('c');
      return;
   }

   @Path("println/int")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnInt(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println(17);
      return;
   }

   @Path("println/long")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnLong(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println(17L);
      return;
   }

   @Path("println/float")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnFloat(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println(17F);
      return;
   }

   @Path("println/double")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void printlnDouble(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.println(17D);
      return;
   }

   @Path("write/array/1")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void writeArray1(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.write("context".getBytes());
      return;
   }

   @Path("write/array/3")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void writeArray3(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.write("context".getBytes(), 1, "context".length() - 1);
      return;
   }

   @Path("write/int")
   @GET
   @Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
   public void writeInt(@Context HttpServletResponse response) throws IOException {
      response.setStatus(200);
      ServletOutputStream os = response.getOutputStream();
      os.write(65); // 'A'
      return;
   }
}
