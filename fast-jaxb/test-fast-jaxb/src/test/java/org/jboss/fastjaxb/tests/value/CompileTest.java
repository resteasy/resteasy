package org.jboss.fastjaxb.tests.value;

import org.jboss.fastjaxb.CodeGenerator;
import org.jboss.fastjaxb.test.value.Person;
import org.jboss.fastjaxb.tests.LocateDirectory;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:07:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompileTest
{
   @Test
   public void testCompile() throws Exception
   {
      CodeGenerator gen = new CodeGenerator();
      String dir = LocateDirectory.getTopDirectory();
      dir += "generated-test-code/src/test/java";
      gen.setDir(dir);
      gen.setPkgName("org.jboss.fastjaxb.generated.value");
      gen.generate(Person.class);
   }
}