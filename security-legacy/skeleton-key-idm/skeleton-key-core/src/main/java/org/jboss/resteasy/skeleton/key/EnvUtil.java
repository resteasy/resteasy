package org.jboss.resteasy.skeleton.key;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.resteasy.microprofile.config.ResteasyConfigFactory;
import org.jboss.resteasy.microprofile.config.ResteasyConfig.SOURCE;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EnvUtil
{
   private static final Pattern p = Pattern.compile("[$][{]([^}]+)[}]");

   /**
    * Replaces any ${} strings with their corresponding environent variable.
    *
    * @param val
    * @return
    */
   public static String replace(String val)
   {
      Matcher matcher = p.matcher(val);
      StringBuffer buf = new StringBuffer();
      while (matcher.find())
      {
         String envVar = matcher.group(1);
         String envVal = ResteasyConfigFactory.getConfig().getValue(envVar, SOURCE.SYSTEM);
         if (envVal == null) envVal = "NOT-SPECIFIED";
         matcher.appendReplacement(buf, envVal.replace("\\", "\\\\"));
      }
      matcher.appendTail(buf);
      return buf.toString();
   }
}
