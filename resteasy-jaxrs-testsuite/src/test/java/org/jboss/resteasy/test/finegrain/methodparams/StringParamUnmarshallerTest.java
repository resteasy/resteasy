package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.FindAnnotation;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringParamUnmarshallerTest extends BaseResourceTest
{

   private static final Logger LOG = Logger.getLogger(StringParamUnmarshallerTest.class);

   @Retention(RetentionPolicy.RUNTIME)
   @StringParameterUnmarshallerBinder(DateFormatter.class)
   public @interface DateFormat
   {
      String value();
   }

   public static class DateFormatter implements StringParameterUnmarshaller<Date>
   {
      private SimpleDateFormat formatter;

      public void setAnnotations(Annotation[] annotations)
      {
         DateFormat format = FindAnnotation.findAnnotation(annotations, DateFormat.class);
         formatter = new SimpleDateFormat(format.value());
      }

      public Date fromString(String str)
      {
         try
         {
            return formatter.parse(str);
         }
         catch (ParseException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   public enum Fruit
   {
      ORANGE,
      PEAR
   }

   public static class Sport
   {
      public String name;

      public static Sport fromString(String str)
      {
         Sport s = new Sport();
         s.name = str;
         return s;
      }


   }

   @Path("/")
   public static class Service
   {
      @GET
      @Produces("text/plain")
      @Path("/datetest/{date}")
      public String get(@PathParam("date") @DateFormat("MM-dd-yyyy") Date date)
      {
         LOG.info(date);
         Calendar c = Calendar.getInstance();
         c.setTime(date);
         Assert.assertEquals(3, c.get(Calendar.MONTH));
         Assert.assertEquals(23, c.get(Calendar.DAY_OF_MONTH));
         Assert.assertEquals(1977, c.get(Calendar.YEAR));
         return date.toString();
      }

      @GET
      @Produces("text/plain")
      @Path("fromstring/{fruit}/{sport}")
      public String getFromString(@PathParam("fruit") Fruit fruit, @PathParam("sport") Sport sport)
      {
         Assert.assertEquals(fruit, Fruit.ORANGE);
         Assert.assertEquals("football", sport.name);
         return sport.name + fruit;
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Service.class);
   }

   @Test
   public void testMe() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/datetest/04-23-1977"));
      LOG.info(request.getTarget(String.class));
   }

   @Test
   public void testMe2() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/fromstring/ORANGE/football"));
      LOG.info(request.getTarget(String.class));
   }
}
