package org.jboss.resteasy.grpc.protobuf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.resteasy.grpc.runtime.protobuf.AssignFromJavabuf;
import org.jboss.resteasy.grpc.runtime.protobuf.AssignToJavabuf;
import org.jboss.resteasy.grpc.runtime.protobuf.TranslateFromJavabuf;
import org.jboss.resteasy.grpc.runtime.protobuf.TranslateToJavabuf;
import org.jboss.resteasy.grpc.runtime.servlet.HttpServletResponseImpl;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

/**
 * Generates a class that can translate back and forth between a Java class and
 * its protobuf representation in Java.
 *
 * For example, given a class {@code CC1_proto} generated by the protobuf compiler from a
 * protobuf descriptor file CC1.proto:
 *
 * <p/>
 * <pre>
 * syntax = "proto3";
 * package io.grpc.classes;
 * option java_package = "io.grpc.classes";
 * option java_outer_classname = "CC1_proto";
 *
 * service CC1Service {
 *   rpc m1 (io_grpc_classes___CC2) returns (String);
 *   rpc m3 (io_grpc_classes___CC4) returns (String);
 * }
 *
 * message io_grpc_classes___CC2 {
 *   int32 j = 1;
 *   io_grpc_classes___CC3 cC3___super = 2;
 * }
 *
 * message io_grpc_classes___CC4 {
 *   string s = 3;
 *   io_grpc_classes___CC5 cc5 = 4;
 * }
 *
 * message io_grpc_classes___CC3 {
 *   string s = 5;
 * }
 *
 * message io_grpc_classes___CC5 {
 *   int32 k = 6;
 * }
 * </pre>
 *
 * {@code JavabufTranslatorGenerator} will generate a {@code CC1_JavabufTranslator} class that
 * can be used as follows:
 *
 * <pre>
 * <p/>
 *    &#064;Test
 *    public void test() {
 *
 *      // 1. Create a CC2.
 *      CC2 cc2 = new CC2("abc", 19);
 *
 *      // 2. Translate to java protobuf form
 *      Message message = CC1_JavabufTranslator.translateToJavabuf(cc2);
 *
 *      // 3. Manually create a java protobuf representation of the same CC2 and demonstrate they're the same.
 *      CC1_proto.io_grpc_classes___CC3 cc3Message =  CC1_proto.io_grpc_classes___CC3.newBuilder().setS("abc").build();
 *      CC1_proto.io_grpc_classes___CC2 cc2Message =  CC1_proto.io_grpc_classes___CC2.newBuilder().setJ(19).setCC3Super(cc3Message).build();
 *      Assert.assertEquals(message, cc2Message);
 *
 *      // 4. A. Translate the java protobuf object created in step 2 back to its original java form.
 *      //    B. Demonstrate it's the same as the java object created in step 1.
 *      CC2 cc2_new = (CC2) CC1_JavabufTranslator.translateFromJavabuf(message);
 *      Assert.assertEquals(cc2,cc2_new);
 *  }
 * </pre>
 */
public class JavabufTranslatorGenerator {

   private static Logger logger = Logger.getLogger(JavabufTranslatorGenerator.class);

   public interface AssignTo {
      void assign(Object from, DynamicMessage.Builder builder);
   }

   public interface AssignFrom {
      void assign(Message message, Object object);
   }

   private static Map<String, Class<?>> PRIMITIVE_WRAPPER_TYPES = new HashMap<String, Class<?>>();
   private static Map<String, String> GET_METHODS = new HashMap<String, String>();

   static {
      PRIMITIVE_WRAPPER_TYPES.put("gByte",      byte.class);
      PRIMITIVE_WRAPPER_TYPES.put("gShort",     short.class);
      PRIMITIVE_WRAPPER_TYPES.put("gInteger",   int.class);
      PRIMITIVE_WRAPPER_TYPES.put("gLong",      long.class);
      PRIMITIVE_WRAPPER_TYPES.put("gFloat",     float.class);
      PRIMITIVE_WRAPPER_TYPES.put("gDouble",    double.class);
      PRIMITIVE_WRAPPER_TYPES.put("gBoolean",   boolean.class);
      PRIMITIVE_WRAPPER_TYPES.put("gCharacter", char.class);
      PRIMITIVE_WRAPPER_TYPES.put("gString",    String.class);
      PRIMITIVE_WRAPPER_TYPES.put("gEmpty",     void.class);

      GET_METHODS.put("Byte",      ".byteValue()");
      GET_METHODS.put("Short",     ".shortValue()");
      GET_METHODS.put("Integer",   ".intValue()");
      GET_METHODS.put("Long",      ".longValue()");
      GET_METHODS.put("Float",     ".floatValue()");
      GET_METHODS.put("Double",    ".doubleValue()");
      GET_METHODS.put("Boolean",   ".booleanValue()");
      GET_METHODS.put("Character", ".toString()");
      GET_METHODS.put("String",    "");
   }

   public static void main(String[] args) {
      if (args == null || args.length != 2) {
         logger.info("need two args:");
         logger.info("  arg[0]: root directory");
         logger.info("  arg[1]: javabuf wrapper class name");
         return;
      }
      try  {
         int index = args[1].lastIndexOf('.');
         String simpleName = index < 0 ? args[1] : args[1].substring(index + 1);
         String translatorClass = simpleName + "_JavabufTranslator";
         Class<?> wrapperClass = Class.forName(args[1] + "_proto", true, Thread.currentThread().getContextClassLoader());
         StringBuilder sb = new StringBuilder();
         classHeader(args, translatorClass, wrapperClass, sb);
         classBody(wrapperClass, sb);
         finishClass(sb);
         writeTranslatorClass(args, translatorClass, sb);
      } catch (Exception e) {
         logger.error(e);
      }
   }

   private static void classHeader(String[] args, String translatorClass, Class<?> wrapperClass, StringBuilder sb) {
      sb.append("package ").append(wrapperClass.getPackage().getName()).append(";\n\n");
      imports(wrapperClass, sb);
      sb.append(   "public class ")
       .append(translatorClass)
       .append(" {\n");
   }

   private static void imports(Class<?> wrapperClass, StringBuilder sb) {
      sb.append("import java.lang.reflect.Field;\n")
        .append("import java.util.ArrayList;\n")
        .append("import java.util.HashMap;\n")
        .append("import java.util.List;\n")
        .append("import java.util.Map;\n")
        .append("import com.google.protobuf.Descriptors;\n")
        .append("import com.google.protobuf.Descriptors.Descriptor;\n")
        .append("import com.google.protobuf.Descriptors.FieldDescriptor;\n")
        .append("import com.google.protobuf.DynamicMessage;\n")
        .append("import com.google.protobuf.Message;\n")
        .append("import ").append(AssignFromJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(AssignToJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(TranslateFromJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(TranslateToJavabuf.class.getCanonicalName()).append(";\n")
        .append("import ").append(HttpServletResponseImpl.class.getCanonicalName()).append(";\n")
        ;
      Class<?>[] classes = wrapperClass.getClasses();
      for (Class<?> clazz: classes) {
         if (clazz.isInterface()) {
            continue;
         }
         String simpleName = clazz.getSimpleName();
         if (PRIMITIVE_WRAPPER_TYPES.containsKey(simpleName)) {
            sb.append("import ").append(clazz.getName().replace("$", ".")).append(";\n");
         } else if ("GeneralEntityMessage".equals(simpleName)
                 || "GeneralReturnMessage".equals(simpleName)
                 || "ServletInfo".equals(simpleName)
                 || "gNewCookie".equals(simpleName)
                 || "gCookie".equals(simpleName)
                 || "gHeader".equals(simpleName)
                 || "FormMap".equals(simpleName)
                 || "FormValues".equals(simpleName)
               ) {
            continue;
         } else {
            sb.append("import ")
            .append(originalClassName(clazz.getName()))
            .append(";\n");
         }
      }
      sb.append("\n");
   }

   private static void classBody(Class<?> wrapperClass, StringBuilder sb) throws Exception {
      Class<?>[] classes = wrapperClass.getClasses();
      privateVariables(sb);
      staticInit(classes, sb);
      publicMethods(sb);
      privateMethods(sb);
      for (Class<?> clazz: classes) {
         if (clazz.isInterface()) {
            continue;
         }
         String simpleName = clazz.getSimpleName();
         if ("GeneralEntityMessage".equals(simpleName) || "GeneralReturnMessage".equals(simpleName)) {
            continue;
         }
         createTranslator(clazz, sb);
      }
   }

   private static void staticInit(Class<?>[] classes, StringBuilder sb) {
      sb.append("   static {\n");
      for (Class<?> clazz: classes) {
         if (clazz.isInterface()) {
            continue;
         }
         String simpleName = clazz.getSimpleName();
         if ("gEmpty".equals(simpleName)
               || "GeneralEntityMessage".equals(simpleName)
               || "GeneralReturnMessage".equals(simpleName)
               || "ServletInfo".equals(simpleName)
               || "gNewCookie".equals(simpleName)
               || "gCookie".equals(simpleName)
               || "gHeader".equals(simpleName)
               || "FormMap".equals(simpleName)
               || "FormValues".equals(simpleName)
               ) {
            continue;
         }
         int i = simpleName.lastIndexOf("___");
         String originalClassName
            = i >= 0 ? simpleName.substring(i + 3)
                     : (PRIMITIVE_WRAPPER_TYPES.containsKey(simpleName) ? simpleName.substring(1) : simpleName);
         sb.append("      toJavabufMap.put(")
           .append(originalClassName)
           .append(".class, new ")
           .append(simpleName)
           .append("_ToJavabuf());\n");
         sb.append("      fromJavabufMap.put(")
           .append("\"" + simpleName + "\"")
           .append(", new ")
           .append(simpleName)
           .append("_FromJavabuf());\n");
      }
      sb.append("   }\n\n");
   }

   private static void publicMethods(StringBuilder sb) {
      sb.append("   public static boolean handlesToJavabuf(Class<?> clazz) {\n")
        .append("      return clazz.isPrimitive() || toJavabufMap.containsKey(clazz);\n")
        .append("   }\n\n")
        .append("   public static boolean handlesFromJavabuf(Class<?> clazz) {\n")
        .append("      return clazz.isPrimitive() || toJavabufMap.containsKey(clazz);\n")
        .append("   }\n\n")
        .append("   public static Message translateToJavabuf(Object o) {\n")
        .append("      TranslateToJavabuf ttj = toJavabufMap.get(o.getClass());\n")
        .append("      if (ttj == null) {\n")
        .append("         throw new RuntimeException(o.getClass() + \" is not recognized\");\n")
        .append("      }\n")
        .append("      return ttj.assignToJavabuf(o);\n")
        .append("   }\n\n")
        .append("   public static Object translateFromJavabuf(Message message) {\n")
        .append("      String s = null;\n")
        .append("      try {\n")
        .append("         s = message.getDescriptorForType().getFullName();\n")
        .append("         s = s.substring(s.lastIndexOf(\".\") + 1);\n")
        .append("         TranslateFromJavabuf tfj = fromJavabufMap.get(s);\n")
        .append("         if (tfj == null) {\n")
        .append("            throw new RuntimeException(message.getClass() + \" is not recognized\");\n")
        .append("         }\n")
        .append("         return tfj.assignFromJavabuf(message);\n")
        .append("      } catch (Exception e) {\n")
        .append("         throw new RuntimeException(e);\n")
        .append("      }\n")
        .append("   }\n\n");
   }

   private static void createTranslator(Class<?> clazz, StringBuilder sb) throws Exception {
      createTranslatorToJavabuf(clazz, sb);
      createTranslatorFromJavabuf(clazz, sb);
   }

   private static void privateVariables(StringBuilder sb) {
      sb.append("   private static Map<Class<?>, TranslateToJavabuf> toJavabufMap = new HashMap<Class<?>, TranslateToJavabuf>();\n");
      sb.append("   private static Map<String, TranslateFromJavabuf> fromJavabufMap = new HashMap<String, TranslateFromJavabuf>();\n\n");
   }

   private static void privateMethods(StringBuilder sb) {
      sb.append("   private static AssignToJavabuf toJavabuf(Class<?> javaClass, FieldDescriptor fd) {\n")
        .append("      try {\n")
        .append("         AssignToJavabuf assignToJavabuf = (obj, messageBuilder) -> {\n")
        .append("            try {\n")
        .append("               if (isSuperClass(fd.getName())) {\n")
        .append("                  Message message = toJavabufMap.get(obj.getClass().getSuperclass()).assignToJavabuf(obj);\n")
        .append("                  messageBuilder.setField(fd, message);\n")
        .append("               } else {\n")
        .append("                  final Field field = javaClass.getDeclaredField(fd.getName());\n")
        .append("                  field.setAccessible(true);\n")
        .append("                  if (!String.class.equals(field.getType()) && toJavabufMap.keySet().contains(field.getType())) {\n")
        .append("                     Message message = toJavabufMap.get(field.getType()).assignToJavabuf(field.get(obj));\n")
        .append("                     messageBuilder.setField(fd, message);\n")
        .append("                  } else {\n")
        .append("                     messageBuilder.setField(fd, field.get(obj));\n")
        .append("                  }\n")
        .append("               }\n")
        .append("            } catch (Exception e) {\n")
        .append("               //\n")
        .append("            }\n")
        .append("         };\n")
        .append("         return assignToJavabuf;\n")
        .append("      } catch (Exception e) {\n")
        .append("         throw new RuntimeException(e);\n")
        .append("      }\n")
        .append("   }\n\n"
      );
      sb.append("   private static AssignFromJavabuf fromJavabuf(Class<?> javaClass, FieldDescriptor fd) {\n")
        .append("      try {\n")
        .append("         AssignFromJavabuf assignFromJavabuf = (message, object) -> {\n")
        .append("            try {\n")
        .append("               if (isSuperClass(fd.getName())) {\n")
        .append("                  String superClassName = javaClassToJavabufClass(javaClass.getSuperclass().getName());\n")
        .append("                  TranslateFromJavabuf t = fromJavabufMap.get(superClassName);\n")
        .append("                  FieldDescriptor sfd = getSuperField(message);\n")
        .append("                  Message superMessage = (Message) message.getField(sfd);\n")
        .append("                  t.assignExistingFromJavabuf(superMessage, object);\n")
        .append("               } else {\n")
        .append("                  final Field field = javaClass.getDeclaredField(fd.getName());\n")
        .append("                  field.setAccessible(true);\n")
        .append("                  if (Descriptors.FieldDescriptor.Type.MESSAGE.equals(fd.getType())\n")
        .append("                      && fromJavabufMap.keySet().contains(fd.getMessageType().getName())) {\n")
        .append("                     Message submessage = (Message) message.getField(fd);\n")
        .append("                     Object obj = fromJavabufMap.get(fd.getMessageType().getName()).assignFromJavabuf(submessage);\n")
        .append("                     field.set(object, obj);\n")
        .append("                  } else {\n")
        .append("                     Object ooo = message.getField(fd);\n")
        .append("                     field.set(object, ooo);\n")
        .append("                  }\n")
        .append("               }\n")
        .append("            } catch (Exception e) {\n")
        .append("               throw new RuntimeException(e);\n")
        .append("            }\n")
        .append("         };\n")
        .append("         return assignFromJavabuf;\n")
        .append("      } catch (Exception e) {\n")
        .append("         throw new RuntimeException(e);\n")
        .append("      }\n")
        .append("   }\n\n"
      );
      sb.append("   private static String javaClassToJavabufClass(String javaClassName) {\n")
        .append("      String javabufClassName = javaClassName.replace(\".\", \"_\");\n")
        .append("      int i = javabufClassName.lastIndexOf(\"_\");\n")
        .append("      javabufClassName = javabufClassName.substring(0, i) + \"___\" + javabufClassName.substring(i + 1);\n")
        .append("      return javabufClassName;\n")
        .append("   }\n\n");
      sb.append("   private static FieldDescriptor getSuperField(Message message) {\n")
        .append("      Map<FieldDescriptor, Object> map = message.getAllFields();\n")
        .append("      for (FieldDescriptor fd : map.keySet()) {\n")
        .append("         if (fd.getName().endsWith(\"___super\")) {\n")
        .append("            return fd;\n")
        .append("         }\n")
        .append("      }\n")
        .append("      return null;\n")
        .append("   }\n\n");
      sb.append("   private static Object messageToObject(Message message) throws ClassNotFoundException {\n")
        .append("      String messageClassName = message.getClass().getName();\n")
        .append("      int i = messageClassName.indexOf(\"___\");\n")
        .append("      String classname = messageClassName.substring(0, i).replaceAll(\"_\", \".\") + \".\" + messageClassName.substring(i + 2);\n")
        .append("      return Class.forName(classname);\n")
        .append("   }\n\n");
      sb.append(
           "   private static boolean isSuperClass(String fieldName) {\n" +
           "      return fieldName.endsWith(\"___super\");\n" +
           "   }\n\n"
      );
   }

   private static void createTranslatorToJavabuf(Class<?> clazz, StringBuilder sb) throws Exception {
      if ("gEmpty".equals(clazz.getSimpleName())
            || "gCookie".equals(clazz.getSimpleName())
            || "gHeader".equals(clazz.getSimpleName())
            || "ServletInfo".equals(clazz.getSimpleName())
            || "gNewCookie".equals(clazz.getSimpleName())
            || "FormMap".equals(clazz.getSimpleName())
            || "FormValues".equals(clazz.getSimpleName())
            ) {
         return;
      }
      sb.append("   static class ")
        .append(fqnify(clazz.getSimpleName())).append("_ToJavabuf implements TranslateToJavabuf {\n")
        .append("      private static Descriptor descriptor = ").append(clazz.getCanonicalName()).append(".getDescriptor();\n")
        .append("      private static DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);\n");
      if (PRIMITIVE_WRAPPER_TYPES.containsKey(clazz.getSimpleName())) {
         String simpleJavabufName = clazz.getSimpleName();
         String simpleJavaName = simpleJavabufName.substring(1);
         sb.append("\n")
         .append("      public Message assignToJavabuf(Object x) {\n")
         .append("         ").append(simpleJavaName).append(" p = (").append(simpleJavaName).append(") x;\n")
         .append("         ").append(clazz.getCanonicalName()).append(".Builder builder = ").append(clazz.getCanonicalName()).append(".newBuilder();\n")
         .append("         return builder.setValue(p").append(GET_METHODS.get(simpleJavaName)).append(").build();\n")
         .append("      }\n");
      } else {
         sb.append("      private static List<AssignToJavabuf> assignList = new ArrayList<AssignToJavabuf>();\n\n")
           .append("      static {\n")
           .append("         for (FieldDescriptor f : descriptor.getFields()) {\n")
           .append("            String name = f.getName();\n")
           .append("            if (descriptor.findFieldByName(name) == null) {\n")
           .append("               continue;\n")
           .append("            }\n")
           .append("            assignList.add(toJavabuf(").append(originalSimpleName(clazz.getSimpleName())).append(".class, descriptor.findFieldByName(name)));\n")
           .append("         }\n")
           .append("      }\n\n")
           .append("      public Message assignToJavabuf(Object c1) {\n")
           .append("         for (AssignToJavabuf assignTo : assignList) {\n")
           .append("            try {\n")
           .append("               assignTo.assign(c1, builder);\n")
           .append("            } catch (Exception e) {\n")
           .append("               throw new RuntimeException(e);\n")
           .append("            }\n")
           .append("         }\n")
           .append("         return builder.build();\n")
           .append("      }\n");
      }
      sb.append("   }\n\n");
   }

   private static void createTranslatorFromJavabuf(Class<?> clazz, StringBuilder sb) throws ClassNotFoundException {
      String originalName = originalSimpleName(clazz.getName());
      if ("gEmpty".equals(originalName)) {
         return;
      }
      if ("AbstractMessage".equals(clazz.getSimpleName())
            || "gCookie".equals(clazz.getSimpleName())
            || "gNewCookie".equals(clazz.getSimpleName())
            || "gHeader".equals(clazz.getSimpleName())
            || "ServletInfo".equals(clazz.getSimpleName())
            || "FormMap".equals(clazz.getSimpleName())
            || "FormValues".equals(clazz.getSimpleName())
            ) {
         return;
      }
      sb.append("   static class ")
        .append(fqnify(clazz.getSimpleName())).append("_FromJavabuf implements TranslateFromJavabuf {\n")
        .append("      private static Descriptor descriptor = ").append(clazz.getCanonicalName()).append(".getDescriptor();\n");
      if (PRIMITIVE_WRAPPER_TYPES.containsKey(originalName)) {
         String javaName = originalName.substring(1);
         if ("gByte".equals(originalName)) {
             sb.append("      public ").append(javaName).append(" assignFromJavabuf(Message message) {\n")
               .append("         FieldDescriptor fd = descriptor.getFields().get(0);\n")
               .append("         return ((Integer) message.getField(fd)).byteValue();\n")
               .append("      }\n\n")
               .append("      public void assignExistingFromJavabuf(Message message, Object obj) { }\n");
          } else if ("gShort".equals(originalName)) {
            sb.append("      public ").append(javaName).append(" assignFromJavabuf(Message message) {\n")
              .append("         FieldDescriptor fd = descriptor.getFields().get(0);\n")
              .append("         return ((Integer) message.getField(fd)).shortValue();\n")
              .append("      }\n\n")
              .append("      public void assignExistingFromJavabuf(Message message, Object obj) { }\n");
          } else if ("gCharacter".equals(originalName)) {
              sb.append("      public ").append(javaName).append(" assignFromJavabuf(Message message) {\n")
                .append("         FieldDescriptor fd = descriptor.getFields().get(0);\n")
                .append("         return ((String) message.getField(fd)).charAt(0);\n")
                .append("      }\n\n")
                .append("      public void assignExistingFromJavabuf(Message message, Object obj) { }\n");
          } else {
            sb.append("      public ").append(javaName).append(" assignFromJavabuf(Message message) {\n")
              .append("         FieldDescriptor fd = descriptor.getFields().get(0);\n")
              .append("         return (").append(javaName).append(") message.getField(fd);\n")
              .append("      }\n\n")
              .append("      public void assignExistingFromJavabuf(Message message, Object obj) { }\n");
         }
      } else {
         sb.append("      private static List<AssignFromJavabuf> assignList = new ArrayList<AssignFromJavabuf>();\n\n")
           .append("      static {\n")
           .append("         for (FieldDescriptor f : descriptor.getFields()) {\n")
           .append("            String name = f.getName();\n")
           .append("            if (descriptor.findFieldByName(name) == null) {\n")
           .append("               continue;\n")
           .append("            }\n")
           .append("            assignList.add(fromJavabuf(").append(originalName).append(".class, descriptor.findFieldByName(name)));\n")
           .append("         }\n")
           .append("      }\n\n")
           .append("      public ").append(originalName).append(" assignFromJavabuf(Message message) {\n");
           int n = findConstructor(clazz, originalName);
           if (n == 0) {
              sb.append("         ").append(originalName).append(" obj = new ").append(originalName).append("();\n");
           }
           else {
              sb.append("         ").append(originalName).append(" obj = new ").append(originalName).append("(");
              for (int i = 0; i < n - 1; i++) {
                 sb.append("null, ");
              }
              sb.append("null);\n");
           }
         sb.append("         for (AssignFromJavabuf assignFrom : assignList) {\n")
           .append("            try {\n")
           .append("               assignFrom.assign(message, obj);\n")
           .append("            } catch (Exception e) {\n")
           .append("               throw new RuntimeException(e);\n")
           .append("            }\n")
           .append("         }\n")
           .append("         return obj;\n")
           .append("      }\n\n")
           .append("      public void assignExistingFromJavabuf(Message message, Object obj) {\n")
           .append("         for (AssignFromJavabuf assignFrom : assignList) {\n")
           .append("            try {\n")
           .append("               assignFrom.assign(message, obj);\n")
           .append("            } catch (Exception e) {\n")
           .append("               throw new RuntimeException(e);\n")
           .append("            }\n")
           .append("         }\n")
           .append("      }\n");
      }
      sb.append("   }\n\n");
   }

   private static void finishClass(StringBuilder sb) {
      sb.append("}\n");
   }

   private static void writeTranslatorClass(String[] args, String translatorClass, StringBuilder sb) throws IOException {
      StringBuilder root = new StringBuilder("target/generated-sources/protobuf/grpc-java/");
      File dir = new File(root.toString());
      if(!dir.exists()) {
         dir.mkdir();
      }
      String pkg = args[1].lastIndexOf(".") < 0 ? "" : args[1].substring(0, args[1].lastIndexOf("."));
      for (String s : pkg.split("\\.")) {
         dir = new File(root.append("/").append(s).toString());
         if (!dir.exists())
         {
            dir.mkdir();
         }
      }
      File file = new File(root.append("/").append(translatorClass).append(".java").toString());
      if (!file.exists()) {
         file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
   }

   private static String fqnify(String s) {
      return s.replace(".", "_");
   }

   private static String originalSimpleName(String s) {
      int i = s.lastIndexOf("___");
      if (i >= 0) {
         return s.substring(i + 3).replace('$', '.');
      }
      // primitive class
      i = s.lastIndexOf("$");
      if (i >= 0) {
         return s.substring(i + 1);
      }
      if (PRIMITIVE_WRAPPER_TYPES.containsKey(s)) {
         return s.substring(1);
      }
      return s;
   }

   private static String originalClassName(String s) {
      int i = s.indexOf("$");
      int j = s.lastIndexOf("___");
      j = j < 0 ? s.length() : j;
      String pkg = s.substring(i + 1, j).replace('_', '.');
      return pkg + "." + originalSimpleName(s);
   }

   private static int findConstructor(Class<?> clazz, String simpleName) throws ClassNotFoundException {
//      int n = clazz.getName().lastIndexOf(".");
//      String className = n < 0 ? simpleName : clazz.getName().substring(0, n + 1) + simpleName;
      String className = javabufToJava(clazz.getName(), simpleName);
      Class<?> originalClazz = Class.forName(className);
      Constructor<?>[] cons = originalClazz.getConstructors();
      Constructor<?> con = cons[0];
      if (cons.length > 1) {
         for (int i = 1; i < cons.length; i++) {
            if (cons[i].getParameterCount() < con.getParameterCount()) {
               con = cons[i];
            }
         }
      }
      return con.getParameterCount();
   }

   private static String javabufToJava(String javabufName, String simpleName) {
      String tmp = javabufName;
      int n = tmp.lastIndexOf("$");
      if (n >= 0) {
         tmp = tmp.substring(n + 1);
      }
      n = tmp.lastIndexOf("___");
      if (n >= 0) {
         tmp = tmp.substring(0, n);
      }
      tmp = tmp.replace("_", ".");
      return tmp + "." + simpleName;
   }
}