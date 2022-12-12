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

package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.test.client.proxy.ProxyPathParamRegexTest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Encoded;
import javax.ws.rs.core.MediaType;

@Path("")
public class ProxyPathParamRegexResource implements ProxyPathParamRegexTest.RegexInterface {
    @GET
    @Path("/{path}/{string:[a-z]?}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPath(@PathParam("path") String path, @PathParam("string") @Encoded String string) {
        return path + string;
    }
}
