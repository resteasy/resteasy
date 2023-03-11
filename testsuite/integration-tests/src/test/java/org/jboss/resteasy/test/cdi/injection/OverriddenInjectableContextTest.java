/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.cdi.injection;

import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.injection.resource.RequiredInjectableContextResource;
import org.jboss.resteasy.test.cdi.injection.resource.RootApplication;
import org.jboss.resteasy.test.cdi.injection.resource.TestProducer;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
public class OverriddenInjectableContextTest {

    @Inject
    Client client;

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, RequiredInjectableContextTest.class.getSimpleName() + ".war")
                .addClasses(RequiredInjectableContextResource.class, RootApplication.class, TestProducer.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml")
                // This can be removed if WFARQ-118 is resolved
                .addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                        // Required for Arquillian
                        new ReflectPermission("suppressAccessChecks"),
                        new PropertyPermission("arquillian.debug", "read"),
                        // Required for JUnit
                        new RuntimePermission("accessDeclaredMembers")),
                        "permissions.xml");
    }

    @Test
    public void overriddenClient() {
        Assert.assertNotNull(client);
        Assert.assertEquals("test value", client.getConfiguration().getProperty("test.client.property"));
    }
}
