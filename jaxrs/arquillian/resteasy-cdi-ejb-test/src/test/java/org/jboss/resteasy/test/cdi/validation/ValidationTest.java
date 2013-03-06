package org.jboss.resteasy.test.cdi.validation;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.cdi.validation.ErroneousResource;
import org.jboss.resteasy.cdi.validation.ErroneousResourceImpl;
import org.jboss.resteasy.cdi.validation.ErrorFreeResource;
import org.jboss.resteasy.cdi.validation.ErrorFreeResourceImpl;
import org.jboss.resteasy.cdi.validation.IntegerProducer;
import org.jboss.resteasy.cdi.validation.JaxRsActivator;
import org.jboss.resteasy.cdi.validation.NumberOneBinding;
import org.jboss.resteasy.cdi.validation.NumberOneErrorBinding;
import org.jboss.resteasy.cdi.validation.NumberTwoBinding;
import org.jboss.resteasy.cdi.validation.ResourceParent;
import org.jboss.resteasy.cdi.validation.SumConstraint;
import org.jboss.resteasy.cdi.validation.SumValidator;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
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
            .addClasses(ErroneousResource.class, ErroneousResourceImpl.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      System.out.println(war.toString(true));
      return war;
   }
   
   @Ignore
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
   
   @Ignore
   @SuppressWarnings("unchecked")
   @Test
   public void testIncorrectInputValues() throws Exception
   {
      log.info("starting testIncorrectInputValues()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/incorrect/test/17");
      ClientResponse<?> response = request.get();
      log.info("status: " + response.getStatus());
      List<List<String>> violations = response.getEntity(List.class);
      log.info("result: " + violations);
      Assert.assertEquals(400, response.getStatus());
      List<String> fieldViolations = violations.get(0);
      Assert.assertEquals(0, fieldViolations.size());
      List<String> propertyViolations = violations.get(1);
      Assert.assertEquals(1, propertyViolations.size());
      Assert.assertTrue(propertyViolations.get(0).indexOf("numberTwo") > -1);
      List<String> classViolations = violations.get(2);
      Assert.assertEquals(1, classViolations.size());
      Assert.assertTrue(classViolations.get(0).indexOf("SumConstraint") > -1);
      List<String> parameterViolations = violations.get(3);
      Assert.assertEquals(1, parameterViolations.size());
      Assert.assertTrue(parameterViolations.get(0).indexOf("ErroneousResource#test(arg0)") > -1);
      List<String> returnValueViolations = violations.get(4);
      Assert.assertEquals(0, returnValueViolations.size());
   }
   
   @Ignore
   @SuppressWarnings("unchecked")
   @Test
   public void testIncorrectReturnValue() throws Exception
   {
      log.info("starting testIncorrectReturnValue()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/correct/test/10");
      ClientResponse<?> response = request.get();
      log.info("status: " + response.getStatus());
      List<List<String>> violations = response.getEntity(List.class);
      log.info("result: " + violations);
      Assert.assertEquals(500, response.getStatus());
      List<String> fieldViolations = violations.get(0);
      Assert.assertEquals(0, fieldViolations.size());
      List<String> propertyViolations = violations.get(1);
      Assert.assertEquals(0, propertyViolations.size());
      List<String> classViolations = violations.get(2);
      Assert.assertEquals(0, classViolations.size());
      List<String> parameterViolations = violations.get(3);
      Assert.assertEquals(0, parameterViolations.size());
      List<String> returnValueViolations = violations.get(4);
      Assert.assertEquals(1, returnValueViolations.size());
      Assert.assertTrue(returnValueViolations.get(0).indexOf("return value") > -1);
   }
}
