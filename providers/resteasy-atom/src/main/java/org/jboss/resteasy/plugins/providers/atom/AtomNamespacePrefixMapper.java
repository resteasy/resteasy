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

package org.jboss.resteasy.plugins.providers.atom;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class AtomNamespacePrefixMapper extends NamespacePrefixMapper {

    static AtomNamespacePrefixMapper INSTANCE = new AtomNamespacePrefixMapper();

    @Override
    public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
        if ("http://www.w3.org/2005/Atom".equals(namespaceUri)) {
            return "atom";
        }
        return suggestion;
    }
}
