package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatchTest
{
   @Test
   public void testMatch()
   {
      Pattern regex = ResourceMethodInvoker.getClassRegexPattern("top");
      String encoded = "top/base/1/resources";
      Matcher matcher = regex.matcher(encoded);
      matcher.find();
      System.out.println(matcher.end());
   }

}
