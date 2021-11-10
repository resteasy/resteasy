package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.ParamConverter;

public class ParamConverterIntegerConverter implements ParamConverter<Integer> {
   public Integer fromString(String str) {
      return Integer.valueOf(str + str);
   }

   public String toString(Integer value) {
      String s = value.toString();
      return s.substring(s.length() / 2);
   }
}
