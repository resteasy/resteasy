package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeMapTest
{
   @Test
   public void testMatching()
   {
      ResteasyProviderFactory.setInstance(new ResteasyProviderFactory());
      //RegisterBuiltin.register(ResteasyProviderFactory.initializeInstance());

      //ResteasyProviderFactory.getInstance().createHeaderDelegate()

      MediaTypeMap<String> map = new MediaTypeMap<String>();
      String defaultPlainText = "defaultPlainText";
      map.add(new MediaType("text", "plain"), defaultPlainText);
      String jaxb = "jaxb";
      map.add(new MediaType("text", "xml"), jaxb);
      String wildcard = "wildcard";
      map.add(new MediaType("*", "*"), wildcard);
      String allText = "allText";
      map.add(new MediaType("text", "*"), allText);
      String app = "app";
      map.add(new MediaType("application", "*"), app);

      List<String> list = map.getPossible(new MediaType("text", "plain"));
      Assert.assertNotNull(list);
      Assert.assertEquals(3, list.size());
      Assert.assertTrue(list.get(0) == defaultPlainText);
      Assert.assertTrue(list.get(1) == allText);
      Assert.assertTrue(list.get(2) == wildcard);

      list = map.getPossible(new MediaType("*", "*"));
      Assert.assertNotNull(list);
      Assert.assertEquals(5, list.size());
      Assert.assertTrue(list.get(0), list.get(0) == defaultPlainText || list.get(0) == jaxb);
      Assert.assertTrue(list.get(1), list.get(1) == defaultPlainText || list.get(1) == jaxb);
      Assert.assertTrue(list.get(2), list.get(2) == allText || list.get(2) == app);
      Assert.assertTrue(list.get(3), list.get(3) == allText || list.get(3) == app);
      Assert.assertTrue(list.get(4), list.get(4) == wildcard);

      list = map.getPossible(new MediaType("text", "*"));
      Assert.assertNotNull(list);
      Assert.assertEquals(4, list.size());
      Assert.assertTrue(list.get(0), list.get(0) == defaultPlainText || list.get(0) == jaxb);
      Assert.assertTrue(list.get(1), list.get(1) == defaultPlainText || list.get(1) == jaxb);
      Assert.assertTrue(list.get(2), list.get(2) == allText);
      Assert.assertTrue(list.get(3), list.get(3) == wildcard);

   }

   @Test
   public void testMatching2()
   {
      //ResteasyProviderFactory.initializeInstance();
      RegisterBuiltin.register(ResteasyProviderFactory.initializeInstance());

      MessageBodyWriter<Integer> writer = ResteasyProviderFactory.getInstance().createMessageBodyWriter(Integer.class, null, null, new MediaType("text", "plain"));
      Assert.assertNotNull(writer);
      Assert.assertEquals(writer.getClass(), DefaultTextPlain.class);
   }


}
