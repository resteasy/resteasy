/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("greet")
public class GreeterResource {

    @GET
    public MultipartRelatedOutput sync() {
        final MultipartRelatedOutput output = new MultipartRelatedOutput();
        output.addPart(new Greeter(), MediaType.TEXT_PLAIN_TYPE);
        return output;
    }

    @GET
    @Path("/async")
    @GreetAsync
    public MultipartRelatedOutput async() {
        final MultipartRelatedOutput output = new MultipartRelatedOutput();
        output.addPart(new Greeter(), MediaType.TEXT_PLAIN_TYPE);
        return output;
    }
}
