/**
 * Copyright 2018 Red Hat, Inc, and individual contributors.
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
package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.spi.RestClientListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;


public class RestClientListeners {

    private RestClientListeners() {
    }

    private static final Collection<RestClientListener> listeners;

    static {
        listeners = loadListeners();
    }

    private static List<RestClientListener> loadListeners() {
        List<RestClientListener> listeners = new ArrayList<>();
        ServiceLoader.load(RestClientListener.class)
                .forEach(listeners::add);
        Collections.unmodifiableCollection(listeners);
        return listeners;
    }

    public static Collection<RestClientListener> get() {
        return listeners;
    }
}
