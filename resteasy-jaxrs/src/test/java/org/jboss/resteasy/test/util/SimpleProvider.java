package org.jboss.resteasy.test.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.spi.StringConverter;

public class SimpleProvider implements ExceptionMapper<NullPointerException>, StringConverter<Integer>
{

   public Response toResponse(NullPointerException exception)
   {
      return null;
   }

   public Integer fromString(String str)
   {
      return null;
   }

   public String toString(Integer value)
   {
      return null;
   }

}
