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

import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.annotations.Separator;

public class SeparatorAnnotationBeanParam {
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    @PathParam("ids")
    @Separator(",")
    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "This is your sentence:" + String.join("", ids);
    }
}
