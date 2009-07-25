package org.jboss.fastjaxb.test.template;

import org.junit.Test;
import org.jboss.fastjaxb.Introspector;
import org.jboss.fastjaxb.ClassCodeGenerator;
import org.jboss.fastjaxb.RootElement;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 5:30:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CodeGeneratorTest
{
   @Test
   public void testGenerator() throws Exception
   {
      ClassCodeGenerator generator = new ClassCodeGenerator();
      generator.setPackageName("org.jboss.test");

      StringWriter writer = new StringWriter();
      PrintWriter pwriter = new PrintWriter(writer);
      generator.setWriter(pwriter);

      Introspector introspector = new Introspector();
      introspector.createMap(Person.class);
      generator.setIntrospector(introspector);

      RootElement rootElement = introspector.getRootElements().get(Person.class);
      generator.setClazz(Person.class);
      generator.setRootElement(rootElement);
      generator.generate();

      pwriter.flush();

      System.out.println(writer.getBuffer().toString());



   }
}
