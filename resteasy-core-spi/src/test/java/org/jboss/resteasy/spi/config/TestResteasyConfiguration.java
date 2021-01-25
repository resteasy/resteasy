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

package org.jboss.resteasy.spi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.resteasy.spi.ResteasyConfiguration;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestResteasyConfiguration implements ResteasyConfiguration {

    static final Map<String, String> PROPS = new HashMap<>();

    static final TestResteasyConfiguration INSTANCE = new TestResteasyConfiguration();

    @Override
    public String getParameter(final String name) {
        return PROPS.get(name);
    }

    @Override
    public Set<String> getParameterNames() {
        return PROPS.keySet();
    }

    @Override
    public String getInitParameter(final String name) {
        return PROPS.get(name);
    }

    @Override
    public Set<String> getInitParameterNames() {
        return PROPS.keySet();
    }
}
