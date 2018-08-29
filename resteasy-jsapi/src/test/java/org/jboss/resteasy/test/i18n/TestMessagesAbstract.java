package org.jboss.resteasy.test.i18n;

import java.lang.reflect.Method;
import java.util.Locale;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.metadata.DefaultResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.junit.Assert;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.jsapi.i18n.Messages;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.DefaultResourceMethod;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 27, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);
   protected static ResourceMethodInvoker testMethod;
   
   static
   {
      try
      {
         Class<?> clazz = TestMessagesAbstract.class;
         Method method = TestMessagesAbstract.class.getMethod("testLocale");
         ResourceClass resourceClass = new DefaultResourceClass(TestMessagesAbstract.class, "path");
         ResourceMethod resourceMethod = new DefaultResourceMethod(resourceClass, method, method);
         ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
         InjectorFactory injectorFactory = new InjectorFactoryImpl();
         ResourceBuilder resourceBuilder = new ResourceBuilder();
         POJOResourceFactory resourceFactory = new POJOResourceFactory(resourceBuilder, clazz);
         testMethod = new ResourceMethodInvoker(resourceMethod, injectorFactory, resourceFactory, providerFactory);
      }
      catch (NoSuchMethodException e)
      {
         LOG.error(e.getMessage(), e);
      }
   }
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/jsapi/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "impossibleToGenerateJsapi", "class", "method"), Messages.MESSAGES.impossibleToGenerateJsapi("class", "method"));
      Assert.assertEquals(getExpected(BASE + "05", "invoker", testMethod), Messages.MESSAGES.invoker(testMethod));
      Assert.assertEquals(getExpected(BASE + "35", "restApiUrl", "http"), Messages.MESSAGES.restApiUrl("http"));
      Assert.assertEquals(getExpected(BASE + "60", "thereAreNoResteasyDeployments"), Messages.MESSAGES.thereAreNoResteasyDeployments());     
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   @Override
   protected String getExpected(String id, String message, Object... args)
   {
      String s = super.getExpected(id, message, args);
      String ss = pruneQuotes(s);
      LOG.info("actual expected: " + ss);
      return ss;
   }
   
   protected String pruneQuotes(String s)
   {
      StringBuffer sb = new StringBuffer();
      boolean sawQuote = false;
      for (int i = 0; i < s.length(); i++)
      {
         char c = s.charAt(i);
         if (sawQuote)
         {
            sawQuote = false;
            sb.append('\'');
            if (c != '\'')
            {
               sb.append(c);
            }
         }
         else if (c == '\'')
         {
            sawQuote = true;
         }
         else
         {
            sb.append(c);
         }
      }
      return sb.toString();
   }
   
   abstract protected Locale getLocale();
}
