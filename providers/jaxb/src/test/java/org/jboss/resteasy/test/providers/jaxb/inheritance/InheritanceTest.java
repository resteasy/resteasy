package org.jboss.resteasy.test.providers.jaxb.inheritance;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InheritanceTest extends BaseResourceTest
{
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(ZooWS.class);
   }

   @Test
   public void testInheritance() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod get = createGetMethod("/zoo");
      int status = client.executeMethod(get);
      Assert.assertEquals(200, status);
   }

}
