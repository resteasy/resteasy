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

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface UndertowConfigurationOptions {

    /**
     * A property which represents a {@link io.undertow.servlet.api.DeploymentInfo} in the
     * {@linkplain jakarta.ws.rs.SeBootstrap.Configuration configuration}.
     */
    String DEPLOYMENT_INFO = "dev.resteasy.embedded.undertow.deployment";

    /**
     * A property which represents a {@link java.util.Map Map&lt;String, String&gt;} of context parameters.
     */
    String CONTEXT_PARAMETERS = "dev.resteasy.embedded.undertow.context.parameters";
}
