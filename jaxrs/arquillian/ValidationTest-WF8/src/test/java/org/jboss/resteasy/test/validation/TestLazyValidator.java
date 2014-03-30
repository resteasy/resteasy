package org.jboss.resteasy.test.validation;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.validation.JaxRsActivator;
import org.jboss.resteasy.validation.TestResourceLazyValidator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Junk 8, 2013
 */
@RunWith(Arquillian.class)
public class TestLazyValidator
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(TestResourceLazyValidator.class)
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   @Ignore
   public void testLazyValidator() throws Exception
   {
      // Valid native constraint
      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/lazy");
      ClientResponse<?> response = request.get();     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity(boolean.class));
   }
}
