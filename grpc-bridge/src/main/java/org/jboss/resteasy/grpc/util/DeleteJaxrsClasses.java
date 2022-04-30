package org.jboss.resteasy.grpc.util;

import java.io.File;

import org.jboss.logging.Logger;
import org.jboss.resteasy.grpc.protobuf.JavabufTranslatorGenerator;

public class DeleteJaxrsClasses {

   private static Logger logger = Logger.getLogger(JavabufTranslatorGenerator.class);

   public static void main(String[] args) {

      if (args.length != 1) {
         logger.info("need one arg");
         logger.info("  arg[0]: source directory");
         return;
      }

      walk(args[0], args[0]);
   }

   public static void walk(String root, String path) {
      File dir = new File(path);
      File[] list = dir.listFiles();
      if (list == null) return;

      for (File f : list) {
         if (f.isDirectory()) {
            walk(root, f.getAbsolutePath());
         }
         else {
            String fileName = f.getAbsoluteFile().toString();
            int n = fileName.indexOf("src/main/java");
            if (n >= 0 && fileName.contains(".java")) {
               fileName = fileName.substring(n + "src/main/java".length());
               fileName = fileName.replace(".java", ".class");
               fileName = root + "/target/classes" + fileName;
               File file = new File(fileName);
               if (file.exists()) {
                  file.delete();
               }
               String subdir = fileName.substring(0, fileName.lastIndexOf("/"));
               String prefix = fileName.substring(0, fileName.lastIndexOf(".")) + "$";
               for (File subdirClass : new File(subdir).listFiles()) {
                  if (subdirClass.toString().startsWith(prefix)) {
                     subdirClass.delete();
                  }
               }
            }
         }
      }
   }
}
