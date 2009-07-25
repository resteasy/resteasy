package org.jboss.fastjaxb;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:19:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CodeGenerator
{
   protected String pkgName;
   protected String dir;

   public String getPkgName()
   {
      return pkgName;
   }

   public void setPkgName(String pkgName)
   {
      this.pkgName = pkgName;
   }

   public String getDir()
   {
      return dir;
   }

   public void setDir(String dir)
   {
      this.dir = dir;
   }

   public void generate(Class... classes) throws Exception
   {
      File file = new File(dir);
      File pkgDir = new File(file, pkgName.replace('.', '/'));
      pkgDir.mkdirs();

      Introspector introspector = new Introspector();

      for (Class clazz : classes)
      {
         introspector.createMap(clazz);
      }

      for (Map.Entry<Class, RootElement> entry : introspector.getRootElements().entrySet())
      {
         ClassCodeGenerator generator = new ClassCodeGenerator();
         generator.setPackageName(pkgName);
         generator.setIntrospector(introspector);
         generator.setClazz(entry.getKey());
         generator.setRootElement(entry.getValue());

         File fp = new File(pkgDir, generator.generated() + ".java");

         PrintWriter writer = new PrintWriter(fp);
         generator.setWriter(writer);
         generator.generate();
         writer.flush();
         writer.close();


      }


   }
}
