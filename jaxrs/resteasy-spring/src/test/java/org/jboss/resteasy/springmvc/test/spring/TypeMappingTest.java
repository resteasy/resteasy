/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.springmvc.test.spring;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;
import static org.junit.Assert.*;

public class TypeMappingTest
{
   
   private TJWSEmbeddedSpringMVCServer server;

   @Test
   public void acceptJSONAndXMLRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/json, application/xml", "application/json");
   }

   @Test
   public void acceptJSONAndXMLRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/json, application/xml", "application/xml");
   }

   @Test
   public void acceptJSONOnlyRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/json", "application/json");
   }

   @Test
   public void acceptJSONOnlyRequestNoProducesNoExtension() throws Exception
   {
      requestAndAssert("noproduces", null, "application/json", "application/json");
   }

   @Test
   public void acceptJSONOnlyRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/json", "application/xml");
   }

   @Test
   public void acceptNullRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", null, "application/json");
   }

   @Test
   public void acceptNullRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", null, "application/xml");
   }

   @Test
   public void acceptXMLAndJSONRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/xml, application/json", "application/json");
   }

   @Test
   public void acceptXMLAndJSONRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/xml, application/json", "application/xml");
   }

   @Test
   public void acceptXMLOnlyRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/xml", "application/json");
   }

   @Test
   public void acceptXMLOnlyRequestNoProducesNoExtension() throws Exception
   {
      requestAndAssert("noproduces", null, "application/xml", "application/xml");
   }

   @Test
   public void acceptXMLOnlyRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/xml", "application/xml");
   }

   @Before
   public void startServer()
   {
      server = new TJWSEmbeddedSpringMVCServer("classpath:spring-typemapping-test-server.xml", TestPortProvider
            .getPort());
      server.start();
   }

   @After
   public void stopServer()
   {
      server.stop();
   }

   private void requestAndAssert(String path, String extension, String accept, String expectedContentType)
         throws Exception
   {
      String url = generateURL("/test/" + path);
      if (extension != null)
      {
         url = url + "." + extension;
      }
      ClientRequest request = new ClientRequest(url);
      if (accept != null)
      {
         request.accept(accept);
      }
      ClientResponse<?> response = null;
      try
      {
         response = request.get(); 
         int status = response.getStatus();
         String contentType = response.getResponseHeaders().getFirst("Content-type");
         assertEquals("Request for " + url + " returned a non-200 status", 200, status);
         assertEquals("Request for " + url + " returned an unexpected content type", expectedContentType, contentType);
      }
      finally
      {
         response.releaseConnection();
      }
   }
}
