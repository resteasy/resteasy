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

package org.jboss.resteasy.client.jaxrs.engines;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.net.ssl.SSLException;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ResteasyDefaultHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {
    static final ResteasyDefaultHttpRequestRetryHandler INSTANCE = new ResteasyDefaultHttpRequestRetryHandler();

    protected ResteasyDefaultHttpRequestRetryHandler() {
        super(3, false, Arrays.asList(
                InterruptedIOException.class,
                UnknownHostException.class,
                ConnectException.class,
                NoRouteToHostException.class,
                SSLException.class
        ));
    }
}
