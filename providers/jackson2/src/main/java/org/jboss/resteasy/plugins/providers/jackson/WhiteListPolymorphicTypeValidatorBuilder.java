package org.jboss.resteasy.plugins.providers.jackson;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;

import org.jboss.resteasy.microprofile.config.ResteasyConfig.SOURCE;
import org.jboss.resteasy.microprofile.config.ResteasyConfigFactory;

import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

public class WhiteListPolymorphicTypeValidatorBuilder extends BasicPolymorphicTypeValidator.Builder
{
   private static final long serialVersionUID = 464558058341488449L;
   private static final String BASE_TYPE_PROP = "resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix";
   private static final String SUB_TYPE_PROP = "resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix";

   public WhiteListPolymorphicTypeValidatorBuilder() {
      super();
      String allowIfBaseType = getProperty(BASE_TYPE_PROP);
      if (allowIfBaseType != null) {
         StringTokenizer st = new StringTokenizer(allowIfBaseType, ",", false);
         while (st.hasMoreTokens()) {
            String t = st.nextToken();
            allowIfBaseType("*".equals(t) ? "" : t);
         }
      }
      String allowIfSubType = getProperty(SUB_TYPE_PROP);
      if (allowIfSubType != null) {
         StringTokenizer st = new StringTokenizer(allowIfSubType, ",", false);
         while (st.hasMoreTokens()) {
            String t = st.nextToken();
            allowIfSubType("*".equals(t) ? "" : t);
         }
      }
   }

   private String getProperty(String propertyName) {
      final SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         return AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
               return ResteasyConfigFactory.getConfig().getValue(propertyName, SOURCE.SYSTEM);
            }
         });
      }
      return ResteasyConfigFactory.getConfig().getValue(propertyName, SOURCE.SYSTEM);
   }
}
