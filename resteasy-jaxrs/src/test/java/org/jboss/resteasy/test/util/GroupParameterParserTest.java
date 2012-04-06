package org.jboss.resteasy.test.util;

import org.jboss.resteasy.util.GroupParameterParser;
import org.jboss.resteasy.util.ParameterParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GroupParameterParserTest
{
   @Test
   public void testOne() throws Exception
   {
      String params = "a=b; c=d; e=\"f,;\" , one=two; three=\"four\"";

      GroupParameterParser parser = new GroupParameterParser();
      List<Map<String, String>> groups = parser.parse(params, ';', ',');
      System.out.println(groups);
      Assert.assertEquals(2, groups.size());
   }

   @Test
   public void testSetAttribute() throws Exception
   {
      String header = "v=1   ;z=33333   ;b=xxxxxxx";
      ParameterParser parser = new ParameterParser();
      String output = parser.setAttribute(header.toCharArray(), 0, header.length(), ';', "b", "");
      System.out.println("<" + output + ">");
      Assert.assertEquals("v=1   ;z=33333   ;b=", output);
   }

   @Test
   public void testSetAttribute2() throws Exception
   {
      String header = "v=1   ;z=33333   ;b=xxxxxxx   ;   foo=bar   ";
      ParameterParser parser = new ParameterParser();
      String output = parser.setAttribute(header.toCharArray(), 0, header.length(), ';', "b", "");
      System.out.println("<" + output + ">");
      Assert.assertEquals("v=1   ;z=33333   ;b=;   foo=bar   ", output);
   }

}
