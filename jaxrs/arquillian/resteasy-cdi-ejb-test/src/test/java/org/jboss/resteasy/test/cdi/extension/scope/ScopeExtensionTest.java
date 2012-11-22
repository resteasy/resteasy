/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.test.cdi.extension.scope;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.extension.scope.ExtensionResource;
import org.jboss.resteasy.cdi.extension.scope.Obsolescent;
import org.jboss.resteasy.cdi.extension.scope.ObsolescentAfterThreeUses;
import org.jboss.resteasy.cdi.extension.scope.ObsolescentAfterTwoUses;
import org.jboss.resteasy.cdi.extension.scope.PlannedObsolescenceContext;
import org.jboss.resteasy.cdi.extension.scope.PlannedObsolescenceExtension;
import org.jboss.resteasy.cdi.extension.scope.PlannedObsolescenceScope;
import org.jboss.resteasy.cdi.injection.JaxRsActivator;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.cdi.util.Utilities;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * CDIScopeExtensionTest tests that Resteasy interacts well with beans in
 * a user defined scope.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@RunWith(Arquillian.class)
public class ScopeExtensionTest
{
   @Inject Logger log;

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-extension-test.war")
      .addClasses(JaxRsActivator.class, UtilityProducer.class, Utilities.class)
      .addClasses(PlannedObsolescenceExtension.class, PlannedObsolescenceScope.class)
      .addClasses(PlannedObsolescenceContext.class, ExtensionResource.class)
      .addClasses(Obsolescent.class, ObsolescentAfterTwoUses.class,  ObsolescentAfterThreeUses.class)
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
      .addAsResource("persistence.xml", "META-INF/persistence.xml")
      .addAsServiceProvider(Extension.class, PlannedObsolescenceExtension.class);
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testObsolescentScope() throws Exception
   {
      log.info("starting testScope()");
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-extension-test/rest/extension/setup/");
      ClientResponse<?> response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      request = new ClientRequest("http://localhost:8080/resteasy-extension-test/rest/extension/test1/");
      response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      request = new ClientRequest("http://localhost:8080/resteasy-extension-test/rest/extension/test2/");
      response = request.post();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
   }
}
