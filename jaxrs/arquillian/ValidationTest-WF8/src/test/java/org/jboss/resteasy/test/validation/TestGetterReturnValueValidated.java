package org.jboss.resteasy.test.validation;

import java.io.Serializable;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.SerializableProvider;
import org.jboss.resteasy.spi.validation.ResteasyViolationException;
import org.jboss.resteasy.spi.validation.Validation;
import org.jboss.resteasy.validation.JaxRsActivator;
import org.jboss.resteasy.validation.TestResourceWithGetterViolation;
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
 * Created June 8, 2013
 */
@RunWith(Arquillian.class)
public class TestGetterReturnValueValidated
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(TestResourceWithGetterViolation.class)
            .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
            .addAsResource("validation-getter.xml", "META-INF/validation.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testReturnValues() throws Exception
   {
      // Valid native constraint
      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/get");
      ClientResponse<?> response = request.get(String.class);
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(500, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      MediaType mediaType = response.getMediaType();
      Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
      Object entity = response.getEntity(Serializable.class);
      System.out.println("entity: " + entity);
      Assert.assertTrue(entity instanceof ResteasyViolationException);
      ResteasyViolationException exception = ResteasyViolationException.class.cast(entity);
      System.out.println(exception.toString());
      countViolations(exception, 1, 0, 0, 0, 0, 1);
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
