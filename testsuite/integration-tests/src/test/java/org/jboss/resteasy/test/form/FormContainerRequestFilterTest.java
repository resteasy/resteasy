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

package org.jboss.resteasy.test.form;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.form.resource.FormContainerRequestFilterFilter;
import org.jboss.resteasy.test.form.resource.FormContainerRequestFilterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpSince RESTEasy 6.2.3.Final
 *          (RESTEASY-567) Verify that PUT and POST endpoints with preceding ContainerRequestFilter
 *          pass the FormParam data.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FormContainerRequestFilterTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormContainerRequestFilterTest.class.getSimpleName());
        war.addClasses(FormContainerRequestFilterTest.class);
        return TestUtil.finishContainerPrepare(war, null,
                FormContainerRequestFilterResource.class,
                FormContainerRequestFilterFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FormContainerRequestFilterTest.class.getSimpleName());
    }

    @Test
    public void testParamPost() {
        try (Client client = ClientBuilder.newClient()) {
            Form form = new Form();
            form.param("fp", "abc xyz");
            Response response = client.target(generateURL("/a")).request()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            Assertions.assertNotNull(response, "Null response was not expected");
            Assertions.assertEquals("abc xyz", response.readEntity(String.class), "Wrong response");
        }
    }

    @Test
    public void testParamPut() {
        try (Client client = ClientBuilder.newClient()) {
            Form form = new Form();
            form.param("fp", "abc xyz");
            Response response = client.target(generateURL("/b")).request()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            Assertions.assertNotNull(response, "Null response was not expected");
            Assertions.assertEquals("abc xyz", response.readEntity(String.class), "Wrong response");
        }
    }
}
