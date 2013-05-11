package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.core.ExtendedCacheControl;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.ext.RuntimeDelegate;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheControlTest
{
   @BeforeClass
   public static void before()
   {
      ResteasyProviderFactory.setInstance(new ResteasyProviderFactory());
   }

   @Test
   public void cacheControlSerialization()
   {
      RuntimeDelegate.HeaderDelegate<CacheControl> hdcc = ResteasyProviderFactory.getInstance()
              .createHeaderDelegate(CacheControl.class);
      CacheControl control = new CacheControl();
      control.setMaxAge(1000);
      control.setSMaxAge(500);
      control.setNoTransform(false);
      control.setPrivate(true);

      String toString = hdcc.toString(control);
      System.out.println(toString);
      CacheControl serialized = hdcc.fromString(toString);

      Assert.assertTrue(serialized.getMaxAge() == 1000);
      Assert.assertTrue(serialized.getSMaxAge() == 500);
      Assert.assertTrue(!serialized.isNoTransform());
      Assert.assertTrue(serialized.isPrivate());
   }


   void assertEqual(CacheControl first, CacheControl second)
   {
      Assert.assertEquals(first.isMustRevalidate(), second.isMustRevalidate());
      Assert.assertEquals(first.isNoCache(), second.isNoCache());
      Assert.assertEquals(first.isNoStore(), second.isNoStore());
      Assert.assertEquals(first.isNoTransform(), second.isNoTransform());
      Assert.assertEquals(first.isPrivate(), second.isPrivate());
      Assert.assertEquals(first.isProxyRevalidate(), second.isProxyRevalidate());
      Assert.assertEquals(first.isPrivate(), second.isPrivate());
      Assert.assertEquals(first.getMaxAge(), second.getMaxAge());
      Assert.assertEquals(first.getSMaxAge(), second.getSMaxAge());
      Assert.assertEquals(first.getNoCacheFields().size(), second.getNoCacheFields().size());
      Assert.assertEquals(first.getPrivateFields().size(), second.getPrivateFields().size());
      for (int i = 0; i < first.getNoCacheFields().size(); i++)
         Assert.assertEquals(first.getNoCacheFields().get(i), second.getNoCacheFields().get(i));
      for (int i = 0; i < first.getPrivateFields().size(); i++)
         Assert.assertEquals(first.getPrivateFields().get(i), second.getPrivateFields().get(i));
      Assert.assertEquals(first.getCacheExtension().size(), second.getCacheExtension().size());
      for (String key : first.getCacheExtension().keySet()) {
         Assert.assertEquals(first.getCacheExtension().get(key), second.getCacheExtension().get(key));
      }
   }

   @Test
   public void testCacheControl()
   {
      CacheControlDelegate delegate = new CacheControlDelegate();

      {
         CacheControl cc = new CacheControl();
         cc.setNoCache(false);
         cc.setNoTransform(true);
         cc.setPrivate(true);
         cc.setMustRevalidate(true);
         cc.setProxyRevalidate(true);
         System.out.println(delegate.toString(cc));
         CacheControl cc2 = delegate.fromString(delegate.toString(cc));
         assertEqual(cc, cc2);

      }

      {
         CacheControl cc = new CacheControl();
         cc.setNoCache(true);
         cc.getNoCacheFields().add("bill");
         cc.getNoCacheFields().add("marc");
         cc.setPrivate(true);
         cc.getPrivateFields().add("yo");
         cc.getCacheExtension().put("foo", "bar");
         cc.setMaxAge(25);
         cc.setSMaxAge(25);
         System.out.println(delegate.toString(cc));
         CacheControl cc2 = delegate.fromString(delegate.toString(cc));
         assertEqual(cc, cc2);

      }
   }

   @Test
   public void testEveryDirectiveAppearsInStringifiedVersion() // TCK requires this
   {
      CacheControl cc = new CacheControl();
      cc.setNoCache(true);
      cc.setPrivate(true);
      cc.setNoStore(true);
      String value = cc.toString();
      assertTrue(value.contains("no-cache"));
      assertTrue(value.contains("no-store"));
      assertTrue(value.contains("private"));
   }

   @Test
   public void testExtendedCacheControl()
   {
      ExtendedCacheControl cc = new ExtendedCacheControl();
      cc.setNoCache(true);
      cc.setPublic(true);
      cc.setNoStore(true);
      String value = cc.toString();
      assertTrue(value.contains("no-cache"));
      assertTrue(value.contains("no-store"));
      assertTrue(value.contains("public"));
   }

}
