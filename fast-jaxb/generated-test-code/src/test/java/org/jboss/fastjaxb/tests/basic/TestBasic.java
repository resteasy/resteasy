package org.jboss.fastjaxb.tests.basic;

import org.jboss.fastjaxb.generated.basic.Person_Parser;
import org.jboss.fastjaxb.spi.Handler;
import org.jboss.fastjaxb.spi.Sax;
import org.jboss.fastjaxb.test.basic.Person;
import org.junit.Test;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:35:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestBasic
{
   @Test
   public void testBasic() throws Exception
   {
      long start = System.currentTimeMillis();
      HashMap<String, Handler> map = new HashMap<String, Handler>();
      map.put("urn:person", new Person_Parser());
      Sax sax = new Sax(map);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();

      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("person.xml");
      parser.parse(is, sax);

      Person person = (Person) sax.getRoot();
      long end = System.currentTimeMillis() - start;
      System.out.println("Time took: " + end);
      System.out.println(person);

   }
}
