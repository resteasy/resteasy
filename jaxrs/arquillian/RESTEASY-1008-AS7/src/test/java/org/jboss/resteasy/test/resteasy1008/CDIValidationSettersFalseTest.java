package org.jboss.resteasy.test.resteasy1008;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.validation.cdi.ResteasyValidationCdiInterceptor;
import org.jboss.resteasy.resteasy1008.SumConstraint;
import org.jboss.resteasy.resteasy1008.SumValidator;
import org.jboss.resteasy.resteasy1008.TestApplication;
import org.jboss.resteasy.resteasy1008.TestResource;
import org.jboss.resteasy.resteasy1008.TestSubResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;


/**
 * RESTEASY-1008
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@RunWith(Arquillian.class)
public class CDIValidationSettersFalseTest extends CDIValidationTestParent
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1008.war")
            .addClasses(CDIValidationTestParent.class)
            .addClasses(TestApplication.class, TestResource.class, TestSubResource.class)
            .addClasses(SumConstraint.class, SumValidator.class)
            .addAsWebInfResource("context/false/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
}
