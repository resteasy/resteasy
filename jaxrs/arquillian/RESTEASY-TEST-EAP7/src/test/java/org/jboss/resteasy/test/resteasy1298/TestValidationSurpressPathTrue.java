package org.jboss.resteasy.test.resteasy1298;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy1298.Foo;
import org.jboss.resteasy.resteasy1298.FooConstraint;
import org.jboss.resteasy.resteasy1298.FooReaderWriter;
import org.jboss.resteasy.resteasy1298.FooValidator;
import org.jboss.resteasy.resteasy1298.JaxRsActivator;
import org.jboss.resteasy.resteasy1298.TestClassConstraint;
import org.jboss.resteasy.resteasy1298.TestClassValidator;
import org.jboss.resteasy.resteasy1298.TestResourceWithAllViolationTypes;
import org.jboss.resteasy.resteasy1298.TestResourceWithReturnValues;
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
            .setWebXML("1298/web_suppress_true.xml")
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
