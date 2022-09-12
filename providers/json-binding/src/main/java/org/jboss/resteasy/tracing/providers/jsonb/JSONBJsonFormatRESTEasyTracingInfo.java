package org.jboss.resteasy.tracing.providers.jsonb;

import org.jboss.resteasy.tracing.api.RESTEasyTracingInfoFormat;
import org.jboss.resteasy.tracing.api.providers.TextBasedRESTEasyTracingInfo;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class JSONBJsonFormatRESTEasyTracingInfo extends TextBasedRESTEasyTracingInfo {
   private Jsonb mapper = JsonbBuilder.create();

   @Override
   public String[] getMessages() {
      try {
         return new String[]{mapper.toJson(messageList)};
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public boolean supports(RESTEasyTracingInfoFormat format) {
      if (format.equals(RESTEasyTracingInfoFormat.JSON))
         return true;
      else
         return false;
   }

}
