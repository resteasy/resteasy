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

package org.jboss.resteasy.test.client.authentication;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(AuthenticationSetupTask.class)
public class Digest512256AuthenticationTest extends AbstractDigestAuthenticationTest {

    public Digest512256AuthenticationTest() {
        super("SHA-512-256");
    }

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, Digest512256AuthenticationTest.class.getSimpleName() + ".war")
                .addClasses(
                        AbstractDigestAuthenticationTest.class,
                        RestActivator.class,
                        UserResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(TestAuth.createJBossWebXml(), "jboss-web.xml")
                .addAsWebInfResource(TestAuth.createWebXml("DIGEST-SHA-512-256"), "web.xml");
    }
}
