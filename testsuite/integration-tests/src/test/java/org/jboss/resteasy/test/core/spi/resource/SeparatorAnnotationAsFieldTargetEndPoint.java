/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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
package org.jboss.resteasy.test.core.spi.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.Separator;

@Path("/separator/field/{ids}")
public class SeparatorAnnotationAsFieldTargetEndPoint {
    @PathParam("ids")
    @Separator(",")
    private List<String> ids;

    @GET
    @Produces("text/plain")
    public String getSentence() {
        StringBuilder sb = new StringBuilder();

        for (String id : ids) {
            sb.append(id);
        }

        return "This is your sentence:" + String.join("", ids);
    }
}
