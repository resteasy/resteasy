package org.jboss.resteasy.test.providers.jaxb.regression.resteasy175;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class KundeTest extends BaseResourceTest
{
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(KundenverwaltungResource.class);
   }

   @Test
   public void testKunde() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TestPortProvider.generateURL("/kundenverwaltung/kunden"));
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      System.out.println(method.getResponseBodyAsString());

   }

}
