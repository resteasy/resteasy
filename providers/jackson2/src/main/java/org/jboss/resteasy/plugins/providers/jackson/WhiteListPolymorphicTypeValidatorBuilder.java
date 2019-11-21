package org.jboss.resteasy.plugins.providers.jackson;

import java.util.StringTokenizer;

import org.eclipse.microprofile.config.Config;
import org.jboss.resteasy.microprofile.config.ResteasyConfigProvider;

import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

public class WhiteListPolymorphicTypeValidatorBuilder extends BasicPolymorphicTypeValidator.Builder
{
   private static final long serialVersionUID = 464558058341488449L;
   private static final String BASE_TYPE_PROP = "resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix";
   private static final String SUB_TYPE_PROP = "resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix";

   public WhiteListPolymorphicTypeValidatorBuilder() {
      super();
      Config c = ResteasyConfigProvider.getConfig();
      String allowIfBaseType = c.getOptionalValue(BASE_TYPE_PROP, String.class).orElse(null);
      if (allowIfBaseType != null) {
         StringTokenizer st = new StringTokenizer(allowIfBaseType, ",", false);
         while (st.hasMoreTokens()) {
            String t = st.nextToken();
            allowIfBaseType("*".equals(t) ? "" : t);
         }
      }
      String allowIfSubType = c.getOptionalValue(SUB_TYPE_PROP, String.class).orElse(null);
      if (allowIfSubType != null) {
         StringTokenizer st = new StringTokenizer(allowIfSubType, ",", false);
         while (st.hasMoreTokens()) {
            String t = st.nextToken();
            allowIfSubType("*".equals(t) ? "" : t);
         }
      }
   }
}
