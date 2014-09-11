package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.validation.Foo;
import org.jboss.resteasy.validation.FooConstraint;
import org.jboss.resteasy.validation.FooReaderWriter;
import org.jboss.resteasy.validation.FooValidator;
import org.jboss.resteasy.validation.JaxRsActivator;
import org.jboss.resteasy.validation.TestClassConstraint;
import org.jboss.resteasy.validation.TestClassValidator;
import org.jboss.resteasy.validation.TestResourceWithAllViolationTypes;
import org.jboss.resteasy.validation.TestResourceWithReturnValues;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 16, 2012
 */
@RunWith(Arquillian.class)
public class TestValidationSurpressPathTrue extends TestValidationSuppressPathParent
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(Foo.class, FooConstraint.class, FooReaderWriter.class, FooValidator.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addClasses(TestResourceWithAllViolationTypes.class, TestResourceWithReturnValues.class)
            .addClass(TestValidationSuppressPathParent.class)
            .setWebXML("web_suppress_true.xml")
            .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testInputViolations() throws Exception
   {
      doTestInputViolations("*", "*", "*", "*");
   }
   
   @Test
   public void testReturnValues() throws Exception
   {
      doTestReturnValueViolations("*");
   }
}
