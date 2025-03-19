/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.jackson2.objectmapper;

import java.net.URI;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
@ServerSetup(LoggingSetupTask.class)
public class DefaultObjectMapperTest extends AbstractObjectMapperTest {

    @ArquillianResource
    private URI uri;

    public DefaultObjectMapperTest() {
        super(false, false);
    }

    @Deployment
    public static WebArchive deployment() {
        return createDeployment(DefaultObjectMapperTest.class, true);
    }

    @Test
    @Override
    public void modifyUser() {
        super.modifyUser();
    }

    @Test
    @Override
    public void modifyContact() throws Exception {
        super.modifyContact();
    }
}
