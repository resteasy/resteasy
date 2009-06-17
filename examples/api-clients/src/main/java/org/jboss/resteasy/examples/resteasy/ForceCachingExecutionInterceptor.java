package org.jboss.resteasy.examples.resteasy;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Calendar;
import java.util.Date;

public class ForceCachingExecutionInterceptor implements
        ClientExecutionInterceptor
{
   private int minutes;

   public ForceCachingExecutionInterceptor(int minutes)
   {
      this.minutes = minutes;
   }

   @SuppressWarnings("unchecked")
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      ClientResponse resp = ctx.proceed();
      MultivaluedMap<String, String> headers = resp.getHeaders();
      String date = headers.getFirst(HttpHeaderNames.DATE);
      if (date != null && headers.getFirst(HttpHeaderNames.EXPIRES) == null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTime(DateUtil.parseDate(date));
         cal.add(Calendar.MINUTE, minutes);
         Date future = cal.getTime();
         headers.add(HttpHeaderNames.EXPIRES, DateUtil.formatDate(future));
      }
      return resp;
   }
}
