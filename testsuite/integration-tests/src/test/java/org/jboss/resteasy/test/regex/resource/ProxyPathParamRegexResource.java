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

package org.jboss.resteasy.test.regex.resource;

import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("")
public class ProxyPathParamRegexResource implements RegexInterface {

    public String getQuestionMarkInMultiplePathParamRegex(@PathParam("path") String path,
            @PathParam("string") @Encoded String string) {
        return path + string;
    }

    public String getSimplePath(@PathParam("string") @Encoded String string) {
        return "simple" + string;
    }

    public String getEncodedQueryParam(@Encoded @QueryParam("m") String queryParam) {
        return "QueryParam" + queryParam;
    }

    public String getQuestionMarkAndQuery(@PathParam("string") String string,
            @QueryParam("m") String queryParam) {
        return "path=" + string + ":query=" + queryParam;
    }

    public String getTwoRegexQuestionMarkTest(@PathParam("lower") String lower,
            @PathParam("upper") String upper) {
        return "lower=" + lower + ":upper=" + upper;
    }

    public String getAsteriskQualiferTest(@PathParam("string") String string,
            @PathParam("path") String path) {
        return "string=" + string + ":path=" + path;
    }

    public String getCurlyBracketQualifierTest(@PathParam("string") String string,
            @PathParam("path") String path) {
        return "string=" + string + ":path=" + path;
    }
}
