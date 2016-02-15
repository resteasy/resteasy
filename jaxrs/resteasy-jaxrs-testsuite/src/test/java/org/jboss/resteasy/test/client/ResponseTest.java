package org.jboss.resteasy.test.client;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseTest
{
   @Test
   public void getLastModifiedTest()
   {
      Date date = Calendar.getInstance().getTime();
      Response response = Response.ok().lastModified(date).build();
      Date responseDate = response.getLastModified();
      System.out.println(date);
      System.out.println(responseDate);
      Assert.assertTrue(date.equals(responseDate));
   }

   public static final DateFormat createDateFormat(TimeZone timezone){
      SimpleDateFormat sdf = new SimpleDateFormat(
              "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      sdf.setTimeZone(timezone);
      return sdf;
   }


   private static String formats(Date date) {
      DateFormat format;
      StringBuilder sb = new StringBuilder();
      for (String tz : TimeZone.getAvailableIDs()) {
         format = createDateFormat(TimeZone.getTimeZone(tz));
         sb.append(format.format(date));
      }
      return sb.toString();
   }

   @Test
   public void expiresTest()
   {
      Date now = Calendar.getInstance().getTime();
      Response.ResponseBuilder rs = Response.ok();
      rs.expires(now);
      Response response = rs.build();
      MultivaluedMap<String, Object> metadata = response.getMetadata();
      Assert.assertNotNull(metadata);
      List<Object> expires = response.getMetadata().get("Expires");
      Assert.assertNotNull(expires);
      boolean condition = false;
      Object fetched = expires.iterator().next();
      if (Date.class.isInstance(fetched))
         condition = ((Date) fetched).compareTo(now) == 0;
      else if (String.class.isInstance(fetched))
         condition = formats(now).contains(fetched.toString());
      else
         throw new RuntimeException("Fetched object not recognised");

      Assert.assertTrue(condition);
   }




}
