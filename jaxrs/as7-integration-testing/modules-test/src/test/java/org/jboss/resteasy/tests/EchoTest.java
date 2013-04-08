package org.jboss.resteasy.tests;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 */
public class EchoTest
{
   @Test
   public void testIt2() throws Exception
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8080/modules-test/jaxrs");
      String data = target.path("test/signed/text").request().get(String.class);
      System.out.println(data);
      client.close();
   }

   @Test
   public void testPKCS7SignedOutput() throws Exception
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8080/modules-test/jaxrs");


      target = target.path("test/signed/pkcs7-signature");
      PKCS7SignatureInput signed = target.request().get(PKCS7SignatureInput.class);
      String output = (String) signed.getEntity(String.class, MediaType.TEXT_PLAIN_TYPE);
      System.out.println(output);
      Assert.assertEquals("hello world", output);
   }

}

