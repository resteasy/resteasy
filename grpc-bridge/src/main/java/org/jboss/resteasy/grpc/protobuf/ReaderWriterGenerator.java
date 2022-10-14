package org.jboss.resteasy.grpc.protobuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.resteasy.grpc.runtime.servlet.HttpServletResponseImpl;

import static java.nio.file.StandardOpenOption.*;

public class ReaderWriterGenerator {

   private static Logger logger = Logger.getLogger(ReaderWriterGenerator.class);
   private static String LS = System.lineSeparator();
   private static Map<String, String> primitives = new HashMap<String, String>();
   private static Set<String> internalClasses = new HashSet<String>();

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

   static {
      internalClasses.add("FormMap");
      internalClasses.add("FormValues");
      internalClasses.add("GeneralEntityMessage");
      internalClasses.add("GeneralReturnMessage");
      internalClasses.add("gCookie");
      internalClasses.add("gEmpty");
      internalClasses.add("gHeader");
      internalClasses.add("gNewCookie");
      internalClasses.add("ServletInfo");
   }

   public static void main(String[] args) {
      if (args == null || args.length != 3) {
         logger.info("need three args:");
         logger.info("  arg[0]: root directory");
         logger.info("  arg[1]: javabuf wrapper class name");
         logger.info("  arg[2]: .proto file prefix");
         return;
      }
      try  {
         String readerWriterClass = args[2] + "_MessageBodyReaderWriter";
         Class<?> wrapperClass = Class.forName(args[1], true, Thread.currentThread().getContextClassLoader());
         StringBuilder sbHeader = new StringBuilder();
         StringBuilder sbBody = new StringBuilder();
         classHeader(args, readerWriterClass, wrapperClass, sbHeader);
         classBody(args, wrapperClass, sbBody);
         finishClass(sbBody);
         writeClass(wrapperClass, args, sbHeader, sbBody);
      } catch (Exception e) {
         logger.error(e);
      }
   }

   private static void classHeader(String[] args, String readerWriterClass, Class<?> wrapperClass, StringBuilder sb) {
      sb.append("package ").append(wrapperClass.getPackage().getName()).append(";" + LS + LS);
      imports(wrapperClass, args[2], sb);
   }

   private static void imports(Class<?> wrapperClass, String rootClass, StringBuilder sb) {
      sb
        .append("import java.io.IOException;" + LS)
        .append("import java.io.InputStream;" + LS)
        .append("import java.io.OutputStream;" + LS)
        .append("import java.lang.annotation.Annotation;" + LS)
        .append("import java.lang.reflect.Type;" + LS)
        .append("import jakarta.annotation.Priority;" + LS)
        .append("import jakarta.ws.rs.Consumes;" + LS)
        .append("import jakarta.ws.rs.Produces;" + LS)
        .append("import jakarta.ws.rs.WebApplicationException;" + LS)
        .append("import jakarta.ws.rs.core.MediaType;" + LS)
        .append("import jakarta.ws.rs.core.MultivaluedMap;" + LS)
        .append("import jakarta.ws.rs.ext.MessageBodyReader;" + LS)
        .append("import jakarta.ws.rs.ext.MessageBodyWriter;" + LS)
        .append("import jakarta.ws.rs.ext.Provider;" + LS)
        .append("import com.google.protobuf.GeneratedMessageV3;" + LS)
        .append("import com.google.protobuf.Any;" + LS)
        .append("import com.google.protobuf.Message;" + LS)
        .append("import com.google.protobuf.CodedInputStream;" + LS)
        .append("import com.google.protobuf.CodedOutputStream;" + LS)
        .append("import ").append("jakarta.servlet.http.HttpServletResponse;" + LS)
        .append("import ").append("org.jboss.resteasy.grpc.runtime.servlet.AsyncMockServletOutputStream;" + LS)
        .append("import ").append(HttpServletResponseImpl.class.getCanonicalName()).append(";" + LS)
        .append("import org.jboss.resteasy.core.ResteasyContext;" + LS)
        ;
      for (Class<?> clazz : wrapperClass.getClasses()) {
         if (clazz.isInterface() || internalClasses.contains(clazz.getSimpleName())) {
            continue;
         }
         if (primitives.containsKey(clazz.getSimpleName())) {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";" + LS);
         } else if ("GeneralEntityMessage".equals(clazz.getSimpleName())
                 || "GeneralReturnMessage".equals(clazz.getSimpleName())
                 || "ServletInfo".equals(clazz.getSimpleName())
                 || "gNewCookie".equals(clazz.getSimpleName())
                 || "gCookie".equals(clazz.getSimpleName())
                 || "gHeader".equals(clazz.getSimpleName())
                 || "FormMap".equals(clazz.getSimpleName())
                 || "FormValues".equals(clazz.getSimpleName())
               ) {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";" + LS);
         } else {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";" + LS);
            sb.append("import ").append(originalClassName(clazz.getName())).append(";" + LS);
         }
      }
      sb.append("" + LS + LS);
   }

   private static void classBody(String[] args, Class<?> wrapperClass, StringBuilder sb) {
      sb.append("@Provider" + LS)
        .append("@Consumes(\"application/grpc-jaxrs;grpc-jaxrs=true\")" + LS)
        .append("@Produces(\"*/*;grpc-jaxrs=true\")" + LS)
        .append("@Priority(Integer.MIN_VALUE)" + LS)
        .append("@SuppressWarnings(\"rawtypes\")" + LS)
        .append("public class ").append(args[2]).append("MessageBodyReaderWriter implements MessageBodyReader<Object>, MessageBodyWriter<Object> {" + LS + LS)
        .append("   @Override" + LS)
        .append("   public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {" + LS)
        .append("      return ").append(args[2]).append("_JavabufTranslator.handlesFromJavabuf(type);" + LS)
        .append("   }" + LS + LS)
        .append("   @SuppressWarnings(\"unchecked\")" + LS)
        .append("   @Override" + LS)
        .append("   public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType," + LS)
        .append("        MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {" + LS)
        .append("      try {" + LS)
        .append("         if (httpHeaders.getFirst(HttpServletResponseImpl.GRPC_RETURN_RESPONSE) != null) {" + LS)
        .append("            Any any =  Any.parseFrom(CodedInputStream.newInstance(entityStream));" + LS)
        .append("            Message m = any.unpack(").append(args[2]).append("_JavabufTranslator.translateToJavabufClass(type));" + LS)
        .append("            return ").append(args[2]).append("_JavabufTranslator.translateFromJavabuf(m);" + LS)
        .append("         } else {" + LS)
        .append("            GeneratedMessageV3 message = getMessage(type, entityStream);" + LS)
        .append("            return ").append(args[2]).append("_JavabufTranslator.translateFromJavabuf(message);" + LS)
        .append("         }" + LS)
        .append("      } catch (Exception e) {" + LS)
        .append("         throw new RuntimeException(e);" + LS)
        .append("      }" + LS)
        .append("   }" + LS + LS)
        .append("   @Override" + LS)
        .append("   public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {" + LS)
       .append("      return ").append(args[2]).append("_JavabufTranslator.handlesToJavabuf(type);" + LS)
        .append("   }" + LS + LS)
        .append("   @Override" + LS)
        .append("   public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType," + LS)
        .append("      MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {" + LS)
        .append("      Message message = ").append(args[2]).append("_JavabufTranslator.translateToJavabuf(t);" + LS)
        .append("      HttpServletResponse servletResponse = ResteasyContext.getContextData(HttpServletResponse.class);" + LS)
        .append("      if (servletResponse != null && servletResponse.getHeader(HttpServletResponseImpl.GRPC_RETURN_RESPONSE) != null) {" + LS)
        .append("         CodedOutputStream cos = CodedOutputStream.newInstance(entityStream);" + LS)
        .append("         Any.pack(message).writeTo(cos);" + LS)
        .append("         cos.flush();" + LS)
        .append("         if (servletResponse.getOutputStream() instanceof AsyncMockServletOutputStream) {" + LS)
        .append("            AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) servletResponse.getOutputStream();" + LS)
        .append("            amsos.release();" + LS)
        .append("            return;" + LS)
        .append("         }" + LS)
        .append("      } else {" + LS)
        .append("         message.writeTo(entityStream);" + LS)
        .append("         entityStream.flush();" + LS)
        .append("      }" + LS)
        .append("      if (servletResponse.getOutputStream() instanceof AsyncMockServletOutputStream) {" + LS)
        .append("         AsyncMockServletOutputStream amsos = (AsyncMockServletOutputStream) servletResponse.getOutputStream();" + LS)
        .append("         amsos.release();" + LS)
        .append("      }" + LS)
        .append("   }" + LS + LS)
        .append("   private static GeneratedMessageV3 getMessage(Class<?> clazz, InputStream is) throws IOException {" + LS);
      Class<?>[] subclasses = wrapperClass.getClasses();
      boolean startElse = false;
      for (int i = 0; i < subclasses.length; i++) {
         if (subclasses[i].isInterface()) {
            continue;
         }
         if (internalClasses.contains(subclasses[i].getSimpleName())) {
            continue;
         }
         if (startElse) {
            sb.append("else ");
         } else {
            startElse = true;
            sb.append("      ");
         }
         String simpleName = subclasses[i].getSimpleName();
         String insert = "";
         if (primitives.containsKey(simpleName) && !primitives.get(simpleName).equals("ignore")) {
            insert = " || " + primitives.get(simpleName) + ".class.equals(clazz)";
         }
         sb.append("if (").append(javabufToJavaClass(simpleName)).append(".class.equals(clazz)").append(insert).append(") {" + LS)
           .append("         return ").append(simpleName).append(".parseFrom(is);" + LS)
           .append("      } ");
      }
      if (subclasses.length > 0) {
         sb.append("else {" + LS)
           .append("         throw new IOException(\"unrecognized class: \" + clazz);" + LS)
           .append("      }" + LS);
      }
      sb.append("   }" + LS + LS);
   }

   private static void finishClass(StringBuilder sb) {
      sb.append("}" + LS);
   }

   private static void writeClass(Class<?> wrapperClass, String[] args, StringBuilder sbHeader, StringBuilder sbBody) throws IOException {
      Path path = Files.createDirectories(Path.of(args[0], wrapperClass.getPackageName().replace(".", "/")));
      path = path.resolve(args[2] + "MessageBodyReaderWriter.java");
      Files.writeString(path, sbHeader.toString(), StandardCharsets.UTF_8);
      Files.writeString(path, sbBody.toString(), StandardCharsets.UTF_8, CREATE, APPEND, WRITE);
   }

   private static String javabufToJavaClass(String classname) {
      int i = classname.indexOf("___");
      if (i >= 0) {
         String simpleName = classname.substring(i + 3);
         if (primitives.containsKey(simpleName) && !"gEmpty".equals(simpleName)) {
            return "java.lang." + simpleName.substring(1);
         }
         return simpleName;
      } else {
         i = classname.indexOf("_INNER_");
         if (i >= 0) {
            return classname.substring(i + "_INNER_".length());
         } else {
            if (primitives.containsKey(classname) && !"gEmpty".equals(classname)) {
               return "java.lang." + classname.substring(1);
            }
            return classname;
         }
      }
   }

   private static String originalSimpleName(String s) {
      int i = s.lastIndexOf("___");
      if (i >= 0) {
         return s.substring(i + "___".length());
      }
      i = s.indexOf("_INNER_");
      if (i >= 0) {
         return s.substring(i + "_INNER_".length());
      }
      return s;
   }

   private static String originalClassName(String s) {
      int i = s.indexOf("$");
      int j = s.lastIndexOf("___");
      j = j < 0 ? s.indexOf("_INNER_") : j;
      j = j < 0 ? s.length() : j;
      String pkg = s.substring(i + 1, j).replace('_', '.');
      return pkg + "." + originalSimpleName(s);
   }
}
