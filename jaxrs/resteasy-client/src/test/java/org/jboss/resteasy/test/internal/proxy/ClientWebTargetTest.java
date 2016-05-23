package org.jboss.resteasy.test.internal.proxy;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;
import org.junit.Test;

import junit.framework.Assert;

import java.util.Collections;

/**
 * @author <a href="mailto:mstefank@redhat.com">Martin Stefanko</a>
 * @version $Revision: 1 $
 */
public class ClientWebTargetTest {

   @Test
   public void propertyNullTest() throws Exception {
      String property = "property";

      ResteasyClient client = new ResteasyClientBuilder().build();
      ClientWebTarget clientWebTarget = (ClientWebTarget) client.target("");

      Assert.assertTrue(client.getConfiguration().getProperties().isEmpty());

      clientWebTarget.property(property, property);

      Assert.assertEquals(Collections.singletonMap(property, property), clientWebTarget.getConfiguration().getProperties());

      try {
         clientWebTarget.property(property, null);
      } catch (NullPointerException ex) {
         Assert.fail("Cannot remove property with null value.");
      }

      Object value = clientWebTarget.getConfiguration().getProperty(property);
      Assert.assertNull(value);
   }
}
