package org.jboss.resteasy.test.i18n;

import java.lang.reflect.Method;
import java.util.Locale;

import junit.framework.Assert;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.resteasy_jsapi.i18n.Messages;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 11, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);
   protected static ResourceMethod testMethod;
   
   static
   {
      try
      {
         Class<?> clazz = TestMessagesAbstract.class;
         Method method = TestMessagesAbstract.class.getMethod("testLocale");
         ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
         InjectorFactory injectorFactory = new InjectorFactoryImpl(providerFactory);
         POJOResourceFactory resourceFactory = new POJOResourceFactory(clazz);
         testMethod = new ResourceMethod(clazz, method, injectorFactory, resourceFactory, providerFactory, null);
      }
      catch (NoSuchMethodException e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/resteasy_jsapi/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "impossibleToGenerateJsapi", "class", "method"), Messages.MESSAGES.impossibleToGenerateJsapi("class", "method"));
      Assert.assertEquals(getExpected(BASE + "05", "invoker", testMethod), Messages.MESSAGES.invoker(testMethod));
      Assert.assertEquals(getExpected(BASE + "55", "startResteasyClient"), Messages.MESSAGES.startResteasyClient());     
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
