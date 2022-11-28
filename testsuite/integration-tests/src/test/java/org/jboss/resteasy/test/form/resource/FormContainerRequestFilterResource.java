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

package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.FormParam;

@Path("/")
public class FormContainerRequestFilterResource {
    @POST
    @Path("a")
    @Consumes("application/x-www-form-urlencoded")
    public String a(@FormParam("fp") String fp) {
        return fp;
    }
    @PUT
    @Path("b")
    @Consumes("application/x-www-form-urlencoded")
    public String b(@FormParam("fp") String fp) {
        return fp;
    }
}

