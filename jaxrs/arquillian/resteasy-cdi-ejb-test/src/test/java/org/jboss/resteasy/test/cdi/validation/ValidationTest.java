package org.jboss.resteasy.test.cdi.validation;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.cdi.validation.InputErrorResource;
import org.jboss.resteasy.cdi.validation.InputErrorResourceImpl;
import org.jboss.resteasy.cdi.validation.ErrorFreeResource;
import org.jboss.resteasy.cdi.validation.ErrorFreeResourceImpl;
import org.jboss.resteasy.cdi.validation.IntegerProducer;
import org.jboss.resteasy.cdi.validation.JaxRsActivator;
import org.jboss.resteasy.cdi.validation.NumberOneBinding;
import org.jboss.resteasy.cdi.validation.NumberOneErrorBinding;
import org.jboss.resteasy.cdi.validation.NumberTwoBinding;
import org.jboss.resteasy.cdi.validation.ResourceParent;
import org.jboss.resteasy.cdi.validation.ReturnValueErrorResource;
import org.jboss.resteasy.cdi.validation.ReturnValueErrorResourceImpl;
import org.jboss.resteasy.cdi.validation.SumConstraint;
import org.jboss.resteasy.cdi.validation.SumValidator;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.validation.cdi.ResteasyValidationCdiInterceptor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@RunWith(Arquillian.class)
public class ValidationTest
{
   @Inject Logger log;
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
            .addClasses(JaxRsActivator.class, UtilityProducer.class, IntegerProducer.class)
            .addClasses(NumberOneBinding.class, NumberOneErrorBinding.class, NumberTwoBinding.class)
            .addClasses(SumConstraint.class, SumValidator.class)
            .addClasses(ResourceParent.class)
            .addClasses(ErrorFreeResource.class, ErrorFreeResourceImpl.class)
            .addClasses(InputErrorResource.class, InputErrorResourceImpl.class)
            .addClasses(ReturnValueErrorResource.class, ReturnValueErrorResourceImpl.class)
            .addClass(ResteasyValidationCdiInterceptor.class)
            .addAsLibrary(new File("target/resteasy-validation-cdi-as7.jar")) // Search
            .addAsWebInfResource("validation/beans.xml", "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testCorrectValues() throws Exception
   {
      log.info("starting testCorrectValues()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/correct/test/7");
      ClientResponse<?> response = request.get();
      log.info("status: " + response.getStatus());
      log.info("response: " + response.getEntity(Integer.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(8 == response.getEntity(Integer.class));
      response.releaseConnection();
   }
   
   @Test
   public void testIncorrectInputValues() throws Exception
   {
      log.info("starting testIncorrectInputValues()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/incorrect/test/17");
      ClientResponse<?> response = request.get();
      log.info("status: " + response.getStatus());
      Object entity = response.getEntity(String.class);
      System.out.println("entity: " + entity);
      Assert.assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      log.info("result: " + e.toString());
      countViolations(e, 4, 0, 2, 1, 1, 0);
      Iterator<ResteasyConstraintViolation> it = e.getPropertyViolations().iterator();
      ResteasyConstraintViolation cv1 = it.next();
      ResteasyConstraintViolation cv2 = it.next();
      boolean b1 = cv1.getPath().indexOf("numberOne") > -1 && cv2.getPath().indexOf("numberTwo") > -1;
      boolean b2 = cv2.getPath().indexOf("numberOne") > -1 && cv1.getPath().indexOf("numberTwo") > -1;
      Assert.assertTrue(b1 || b2);
      cv1 = e.getClassViolations().iterator().next();
      Assert.assertTrue(cv1.getMessage().indexOf("SumConstraint") > -1);
      cv1 = e.getParameterViolations().iterator().next();  
      Assert.assertTrue(cv1.getPath().indexOf("InputErrorResourceImpl#test(arg0)") > -1);
   }
   
   @Test
   public void testIncorrectReturnValue() throws Exception
   {
      log.info("starting testIncorrectReturnValue()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/return/test");
      ClientResponse<?> response = request.get();
      log.info("status: " + response.getStatus());
      Object entity = response.getEntity(String.class);
      System.out.println("entity: " + entity);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
      log.info("result: " + e.toString());
      Assert.assertEquals(500, response.getStatus());
      countViolations(e, 1, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().indexOf("") > -1);
   }
   
   private void countViolations(ResteasyViolationException e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount,       e.getViolations().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}
