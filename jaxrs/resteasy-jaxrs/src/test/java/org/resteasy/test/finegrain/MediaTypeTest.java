package org.resteasy.test.finegrain;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.MediaTypeHelper;
import org.resteasy.util.WeightedMediaType;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeTest
{
   @BeforeClass
   public static void start()
   {
      ResteasyProviderFactory.initializeInstance();
   }

   @Test
   public void testParsing()
   {

      MediaType mediaType;

      mediaType = MediaType.parse("application/xml");
      Assert.assertEquals("application", mediaType.getType());
      Assert.assertEquals("xml", mediaType.getSubtype());

      mediaType = MediaType.parse("text/*;q=0.3");
      Assert.assertEquals("text", mediaType.getType());
      Assert.assertEquals("*", mediaType.getSubtype());
      Assert.assertTrue(mediaType.isWildcardSubtype());
      Assert.assertEquals(1, mediaType.getParameters().size());
      Assert.assertEquals("0.3", mediaType.getParameters().get("q"));

      mediaType = MediaType.parse("text/html;level=2;q=0.4");
      Assert.assertEquals("text", mediaType.getType());
      Assert.assertEquals("html", mediaType.getSubtype());
      Assert.assertEquals(2, mediaType.getParameters().size());
      Assert.assertEquals("0.4", mediaType.getParameters().get("q"));
      Assert.assertEquals("2", mediaType.getParameters().get("level"));

      MediaTypeHeaderDelegate delegate = new MediaTypeHeaderDelegate();
      String str = delegate.toString(mediaType);
      mediaType = MediaType.parse(str);
      Assert.assertEquals("text", mediaType.getType());
      Assert.assertEquals("html", mediaType.getSubtype());
      Assert.assertEquals(2, mediaType.getParameters().size());
      Assert.assertEquals("0.4", mediaType.getParameters().get("q"));
      Assert.assertEquals("2", mediaType.getParameters().get("level"));
   }

   @Test
   public void testSort()
   {
      MediaType[] array = {
              MediaType.parse("text/*"),
              MediaType.parse("text/html"),
              MediaType.parse("text/html;level=1"),
              MediaType.parse("*/*")
      };
      List<MediaType> list = new ArrayList<MediaType>();
      list.add(array[0]);
      list.add(array[1]);
      list.add(array[2]);
      list.add(array[3]);

      MediaTypeHelper.sortByWeight(list);

      Assert.assertTrue(list.get(0).toString(), array[2] == list.get(0));
      Assert.assertTrue(array[1] == list.get(1));
      Assert.assertTrue(array[0] == list.get(2));
      Assert.assertTrue(array[3] == list.get(3));


   }

   @Test
   public void testSort2()
   {
      MediaType[] array = {
              MediaType.parse("text/*;q=0.3"),
              MediaType.parse("text/html;q=0.7"),
              MediaType.parse("text/html;level=1"),
              MediaType.parse("text/html;level=2;q=0.4"),
              MediaType.parse("*/*;q=0.5")
      };
      List<MediaType> list = new ArrayList<MediaType>();
      list.add(array[0]);
      list.add(array[1]);
      list.add(array[2]);
      list.add(array[3]);
      list.add(array[4]);

      MediaTypeHelper.sortByWeight(list);

      Assert.assertTrue(array[2] == list.get(0));
      Assert.assertTrue(array[1] == list.get(1));
      Assert.assertTrue(array[4] == list.get(2));
      Assert.assertTrue(array[3] == list.get(3));
      Assert.assertTrue(array[0] == list.get(4));


   }

   @Test
   public void testWeightedSort()
   {
      WeightedMediaType[] array = {
              WeightedMediaType.parse("text/*"),
              WeightedMediaType.parse("text/html"),
              WeightedMediaType.parse("text/html;level=1"),
              WeightedMediaType.parse("*/*")
      };
      List<WeightedMediaType> list = new ArrayList<WeightedMediaType>();
      list.add(array[0]);
      list.add(array[1]);
      list.add(array[2]);
      list.add(array[3]);

      Collections.sort(list);

      Assert.assertTrue(list.get(0).toString(), array[2] == list.get(0));
      Assert.assertTrue(array[1] == list.get(1));
      Assert.assertTrue(array[0] == list.get(2));
      Assert.assertTrue(array[3] == list.get(3));


   }

   @Test
   public void testWeightedSort2()
   {
      WeightedMediaType[] array = {
              WeightedMediaType.parse("text/*;q=0.3"),
              WeightedMediaType.parse("text/html;q=0.7"),
              WeightedMediaType.parse("text/html;level=1"),
              WeightedMediaType.parse("text/html;level=2;q=0.4"),
              WeightedMediaType.parse("*/*;q=0.5")
      };
      List<WeightedMediaType> list = new ArrayList<WeightedMediaType>();
      list.add(array[0]);
      list.add(array[1]);
      list.add(array[2]);
      list.add(array[3]);
      list.add(array[4]);

      Collections.sort(list);

      Assert.assertTrue(array[2] == list.get(0));
      Assert.assertTrue(array[1] == list.get(1));
      Assert.assertTrue(array[4] == list.get(2));
      Assert.assertTrue(array[3] == list.get(3));
      Assert.assertTrue(array[0] == list.get(4));


   }
}
