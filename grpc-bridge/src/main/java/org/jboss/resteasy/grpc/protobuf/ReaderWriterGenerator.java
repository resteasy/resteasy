package org.jboss.resteasy.grpc.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.resteasy.grpc.runtime.servlet.HttpServletResponseImpl;

public class ReaderWriterGenerator {

   private static Logger logger = Logger.getLogger(ReaderWriterGenerator.class);
   private static Map<String, String> primitives = new HashMap<String, String>();

   static {
      primitives.put("gBoolean",   "boolean");
      primitives.put("gByte",      "byte");
      primitives.put("gCharacter", "char");
      primitives.put("gDouble",    "double");
      primitives.put("gEmpty",     "ignore");
      primitives.put("gFloat",     "float");
      primitives.put("gInteger",   "int");
      primitives.put("gLong",      "long");
      primitives.put("gShort",     "short");
      primitives.put("gString",    "ignore");
   }

   public static void main(String[] args) {
      if (args.length != 2) {
         logger.info("need two args:");
         logger.info("  arg[0]: javabuf wrapper class name");
         logger.info("  arg[1]: .proto file prefix");
         return;
      }
      try  {
         String readerWriterClass = args[1] + "_MessageBodyReaderWriter";
         Class<?> wrapperClass = Class.forName(args[0], true, Thread.currentThread().getContextClassLoader());
         StringBuilder sbHeader = new StringBuilder();
         StringBuilder sbBody = new StringBuilder();
         classHeader(args, readerWriterClass, wrapperClass, sbHeader);
         classBody(args, wrapperClass, sbBody);
         finishClass(sbBody);
         writeClass(wrapperClass, args[1], sbHeader, sbBody);
      } catch (Exception e) {
         logger.error(e);
      }
   }

   private static void classHeader(String[] args, String readerWriterClass, Class<?> wrapperClass, StringBuilder sb) {
      sb.append("package ").append(wrapperClass.getPackage().getName()).append(";\n\n");
      imports(wrapperClass, args[1], sb);
   }

   private static void imports(Class<?> wrapperClass, String rootClass, StringBuilder sb) {
      sb.append("import java.io.ByteArrayOutputStream;\n")
        .append("import java.io.IOException;\n")
        .append("import java.io.InputStream;\n")
        .append("import java.io.OutputStream;\n")
        .append("import java.lang.annotation.Annotation;\n")
        .append("import java.lang.reflect.Type;\n")
        .append("import jakarta.annotation.Priority;\n")
        .append("import jakarta.servlet.ServletConfig;\n")
        .append("import jakarta.ws.rs.Consumes;\n")
        .append("import jakarta.ws.rs.Produces;\n")
        .append("import jakarta.ws.rs.WebApplicationException;\n")
        .append("import jakarta.ws.rs.core.MediaType;\n")
        .append("import jakarta.ws.rs.core.MultivaluedMap;\n")
        .append("import jakarta.ws.rs.ext.MessageBodyReader;\n")
        .append("import jakarta.ws.rs.ext.MessageBodyWriter;\n")
        .append("import jakarta.ws.rs.ext.Provider;\n")
        .append("import com.google.protobuf.GeneratedMessageV3;\n")
        .append("import com.google.protobuf.Any;\n")
        .append("import com.google.protobuf.Message;\n")
        .append("import com.google.protobuf.CodedInputStream;\n")
        .append("import com.google.protobuf.CodedOutputStream;\n")
        .append("import ").append("jakarta.servlet.http.HttpServletResponse;\n")
        .append("import ").append("org.jboss.resteasy.grpc.servlet.runtime.AsyncMockServletOutputStream;\n")
        .append("import ").append(HttpServletResponseImpl.class.getCanonicalName()).append(";\n")
        .append("import ").append(wrapperClass.getPackageName()).append(".").append(rootClass).append("_JavabufTranslator;\n")
        .append("import org.jboss.resteasy.core.ResteasyContext;\n")
        .append("import org.jboss.resteasy.grpc.servlet.runtime.ServletConfigWrapper;\n")
        ;
      for (Class<?> clazz : wrapperClass.getClasses()) {
         if (clazz.isInterface()) {
            continue;
         }
         if (primitives.containsKey(clazz.getSimpleName())) {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";\n");
         } else if ("GeneralEntityMessage".equals(clazz.getSimpleName())
                 || "GeneralReturnMessage".equals(clazz.getSimpleName())
                 || "ServletInfo".equals(clazz.getSimpleName())
                 || "gNewCookie".equals(clazz.getSimpleName())
                 || "gCookie".equals(clazz.getSimpleName())
                 || "gHeader".equals(clazz.getSimpleName())
                 || "FormMap".equals(clazz.getSimpleName())
                 || "FormValues".equals(clazz.getSimpleName())
               ) {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";\n");
         } else {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";\n");
            sb.append("import ").append(originalClassName(clazz.getName())).append(";\n");
         }
      }
      sb.append("\n\n");
   }

   private static void classBody(String[] args, Class<?> wrapperClass, StringBuilder sb) {
      sb.append("@Provider\n")
        .append("@Consumes(\"application/grpc-jaxrs;grpc-jaxrs=true\")\n")
        .append("@Produces(\"*/*;grpc-jaxrs=true\")\n")
        .append("@Priority(Integer.MIN_VALUE)\n")
        .append("@SuppressWarnings(\"rawtypes\")\n")
        .append("public class ").append(args[1]).append("MessageBodyReaderWriter implements MessageBodyReader<Object>, MessageBodyWriter<Object> {\n\n")
        .append("   @Override\n")
        .append("   public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {\n")
        .append("      return ").append(args[1]).append("_JavabufTranslator.handlesFromJavabuf(type);\n")
        .append("   }\n\n")
        .append("   @SuppressWarnings(\"unchecked\")\n")
        .append("   @Override\n")
        .append("   public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType,\n")
        .append("        MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {\n")
        .append("      try {\n")
        .append("      if (httpHeaders.getFirst(HttpServletResponseImpl.GRPC_RETURN_RESPONSE) != null) {\n")
        .append("         return Any.parseFrom(CodedInputStream.newInstance(entityStream));\n")
        .append("      } else {\n")
        .append("         GeneratedMessageV3 message = getMessage(type, entityStream);\n")
        .append("         return ").append(args[1]).append("_JavabufTranslator.translateFromJavabuf(message);\n")
        .append("      }\n")
        .append("      } catch (Exception e) {\n")
        .append("         throw new RuntimeException(e);\n")
        .append("      }\n")
        .append("   }\n\n")
        .append("   @Override\n")
        .append("   public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {\n")
//        .append("      ServletConfig servletConfig = ResteasyContext.getContextData(ServletConfig.class);\n")
//        .append("      return servletConfig != null && servletConfig.getInitParameter(ServletConfigWrapper.GRPC_JAXRS) != null && CC1_JavabufTranslator.handlesToJavabuf(type);\n")
        .append("      return ").append(args[1]).append("_JavabufTranslator.handlesToJavabuf(type);\n")
        .append("   }\n\n")
        .append("   @Override\n")
        .append("   public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,\n")
        .append("      MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {\n")
        .append("      Message message = ").append(args[1]).append("_JavabufTranslator.translateToJavabuf(t);\n")
        .append("      HttpServletResponse servletResponse = ResteasyContext.getContextData(HttpServletResponse.class);\n")
        .append("      if (servletResponse != null && servletResponse.getHeader(HttpServletResponseImpl.GRPC_RETURN_RESPONSE) != null) {\n")
        .append("         CodedOutputStream cos = CodedOutputStream.newInstance(entityStream);\n")
        .append("         Any.pack(message).writeTo(cos);\n")
        .append("         cos.flush();\n")
        .append("         if (servletResponse.getOutputStream() instanceof AsyncMockServletOutputStream) {\n")
        .append("            AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) servletResponse.getOutputStream();\n")
        .append("            amsos.release();\n")
        .append("            return;\n")
        .append("         }\n")
        .append("      } else {\n")
        .append("         message.writeTo(entityStream);\n")
        .append("         entityStream.flush();\n")
        .append("      }\n")
        .append("      if (servletResponse.getOutputStream() instanceof AsyncMockServletOutputStream) {\n")
        .append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) servletResponse.getOutputStream();\n")
        .append("         amsos.release();\n")
        .append("      }\n")
        .append("   }\n\n")
        .append("   private static GeneratedMessageV3 getMessage(Class<?> clazz, InputStream is) throws IOException {\n");
      Class<?>[] subclasses = wrapperClass.getClasses();
      boolean startElse = false;
      for (int i = 0; i < subclasses.length; i++) {
         if (subclasses[i].isInterface()) {
            continue;
         }
         if (startElse) {
            sb.append("else ");
         } else {
            startElse = true;
         }
         String simpleName = subclasses[i].getSimpleName();
         String insert = "";
         if (primitives.containsKey(simpleName) && !primitives.get(simpleName).equals("ignore")) {
            insert = " || " + primitives.get(simpleName) + ".class.equals(clazz)";
         }
         sb.append("      if (").append(javabufToJavaClass(simpleName)).append(".class.equals(clazz)").append(insert).append(") {\n")
           .append("         return ").append(simpleName).append(".parseFrom(is);\n")
           .append("      } ");
      }
      if (subclasses.length > 0) {
         sb.append("else {\n")
           .append("         throw new IOException(\"unrecognized class: \" + clazz);\n")
           .append("      }\n");
      }
      sb.append("   }\n\n");

      startElse = false;
      sb.append("   private static GeneratedMessageV3 unpackMessage(Class<?> clazz, Any any) throws IOException {\n");
      for (int i = 0; i < subclasses.length; i++) {
         if (subclasses[i].isInterface()) {
            continue;
         }
         if (startElse) {
            sb.append("else ");
         } else {
            startElse = true;
         }
         String simpleName = subclasses[i].getSimpleName();
         String insert = "";
         if (primitives.containsKey(simpleName) && !primitives.get(simpleName).equals("ignore")) {
            insert = " || " + primitives.get(simpleName) + ".class.equals(clazz)";
         }
         sb.append("      if (").append(javabufToJavaClass(simpleName)).append(".class.equals(clazz)").append(insert).append(") {\n")
           .append("         return any.unpack(").append(simpleName).append(".class);\n")
           .append("      } ");
      }
      if (subclasses.length > 0) {
         sb.append("else {\n")
           .append("         throw new IOException(\"unrecognized class: \" + clazz);\n")
           .append("      }\n");
      }
      sb.append("   }\n\n");
   }

   private static void finishClass(StringBuilder sb) {
      sb.append("}\n");
   }

   private static void writeClass(Class<?> wrapperClass, String prefix, StringBuilder sbHeader, StringBuilder sbBody) throws IOException {
      String packageName = wrapperClass.getPackageName();
      String path = "";
      for (String s : ("target/generated-sources/protobuf/grpc-java/" + packageName.replace(".", "/")).split("/")) {
         path += s;
         File dir = new File(path);
         if(!dir.exists()){
            dir.mkdir();
         }
         path += "/";
      }
      File file = new File(path + prefix + "MessageBodyReaderWriter.java");
      if (file.exists()) {
         return;
      }
      file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sbHeader.toString());
      bw.write(sbBody.toString());
      bw.close();
   }

   private static String javabufToJavaClass(String classname) {
      int i = classname.indexOf("___");
      String simpleName = i < 0 ? classname : classname.substring(i + 3);
      if (primitives.containsKey(simpleName) && !"gEmpty".equals(simpleName)) {
         return "java.lang." + simpleName.substring(1);
      }
      return simpleName;
   }

   private static String originalSimpleName(String s) {
      int i = s.lastIndexOf("___");
      return i < 0 ? s : s.substring(i + 3);
   }

   private static String originalClassName(String s) {
      int i = s.indexOf("$");
      int j = s.lastIndexOf("___");
      j = j < 0 ? s.length() : j;
      String pkg = s.substring(i + 1, j).replace('_', '.');
      return pkg + "." + originalSimpleName(s);
   }
}
