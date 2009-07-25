package org.jboss.fastjaxb.test.template;

import org.junit.Test;
import org.jboss.fastjaxb.template.Handler;
import org.jboss.fastjaxb.template.Sax;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 1:35:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateTest
{
   @Test
   public void testTemplate() throws Exception
   {
      HashMap<String, Handler> map = new HashMap<String, Handler>();
      map.put("urn:person", new PersonParser());
      Sax sax = new Sax(map);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();

      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("person.xml");
      parser.parse(is, sax);

      Person person = (Person)sax.getRoot();
      System.out.println(person);



   }
}
