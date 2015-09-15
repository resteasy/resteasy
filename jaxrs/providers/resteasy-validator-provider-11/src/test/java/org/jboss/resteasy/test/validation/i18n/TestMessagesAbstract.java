package org.jboss.resteasy.test.validation.i18n;

import java.util.Locale;

import javax.validation.ElementKind;
import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ConstraintType;
import org.jboss.resteasy.plugins.validation.i18n.Messages;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 24, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/validation/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "05", "expectTwoNonNullMethods"), Messages.MESSAGES.expectTwoNonNullMethods());
      Assert.assertEquals(getExpected(BASE + "30", "unexpectedPathNode", ElementKind.BEAN), Messages.MESSAGES.unexpectedPathNode(ElementKind.BEAN));
      Assert.assertEquals(getExpected(BASE + "40", "unexpectedViolationType", ConstraintType.Type.CLASS), Messages.MESSAGES.unexpectedViolationType(ConstraintType.Type.CLASS));
      Assert.assertEquals(getExpected(BASE + "60", "validateOnExceptionOnMultipleMethod"), Messages.MESSAGES.validateOnExceptionOnMultipleMethod());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
