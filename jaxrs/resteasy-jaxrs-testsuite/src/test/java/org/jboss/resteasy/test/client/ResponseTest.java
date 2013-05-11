package org.jboss.resteasy.test.client;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;

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


}
