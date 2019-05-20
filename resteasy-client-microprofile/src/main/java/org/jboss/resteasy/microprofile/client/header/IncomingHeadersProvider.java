/**
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.microprofile.client.header;

import javax.ws.rs.core.MultivaluedMap;

/**
 * MicroProfile Rest Client 1.2 requires headers incoming from an external request to be available for an outgoing
 * request in order to have a possibility to pass them through.
 *
 * To make it work, vendors using SmallRye Rest Client should implement this interface
 * and register it via Service Loader mechanism.
 *
 * @see ClientHeadersRequestFilter
 */
public interface IncomingHeadersProvider {
    /**
     * @return a list of headers that came from a JAX-RS request that triggered the current call
     */
    MultivaluedMap<String, String> getIncomingHeaders();
}
