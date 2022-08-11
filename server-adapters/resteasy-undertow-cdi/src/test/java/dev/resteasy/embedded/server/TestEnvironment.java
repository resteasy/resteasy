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

package dev.resteasy.embedded.server;

import jakarta.ws.rs.SeBootstrap;

import org.jboss.jandex.Index;
import org.jboss.resteasy.core.se.ConfigurationOption;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class TestEnvironment {
    static final int TIMEOUT = Integer.parseInt(System.getProperty("dev.resteasy.embedded.startup.timeout", "120"));

    static SeBootstrap.Configuration createConfig(final Index index) {
        return SeBootstrap.Configuration.builder()
                .property(ConfigurationOption.JANDEX_INDEX.key(), index)
                .build();
    }
}
