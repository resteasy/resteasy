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

package dev.resteasy.tck;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyApplicationArchiveProcessor implements ApplicationArchiveProcessor {
    @Override
    public void process(final Archive<?> archive, final TestClass testClass) {
        if ("jaxrs_ee_rs_container_requestcontext_security_web.war".equals(archive.getName())) {
            WebArchive webArchive = (WebArchive) archive;
            webArchive.addAsWebInfResource("jboss-web.xml", "jboss-web.xml");
        } else if ("jaxrs_ee_core_securitycontext_basic_web.war".equals(archive.getName())) {
            WebArchive webArchive = (WebArchive) archive;
            webArchive.addAsWebInfResource("jboss-web.xml", "jboss-web.xml");
        }
    }
}
