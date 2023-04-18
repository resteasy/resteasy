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

package dev.resteasy.client.util.authentication.digest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DigestScheme {
    // https://datatracker.ietf.org/doc/html/rfc7616#section-3.4
    final String realm;
    final String nonce;
    final String opaque;
    final Algorithm algorithm;
    final String qop;
    final boolean stale;
    final AtomicInteger nc;
    final boolean userhash;

    private DigestScheme(final String realm, final String qop, final String nonce, final String opaque,
            final Algorithm algorithm, final boolean stale, final boolean userhash) {
        this.realm = realm;
        this.nonce = nonce;
        this.opaque = opaque;
        this.qop = qop;
        this.algorithm = algorithm;
        this.stale = stale;
        this.nc = new AtomicInteger();
        this.userhash = userhash;
    }

    static DigestScheme of(final String headerValue) {
        final int i = headerValue.trim().indexOf(' ');
        if (i <= 0) {
            return null;
        }
        final String challenge = headerValue.substring(i + 1);
        String realm = null;
        String nonce = null;
        String opaque = null;
        String qop = null;
        String algorithm = null;
        boolean stale = false;
        boolean userhash = false;

        final Map<String, String> properties = parse(challenge);
        for (Map.Entry<String, String> property : properties.entrySet()) {
            final String key = property.getKey();
            final String val = property.getValue();
            if ("qop".equalsIgnoreCase(key)) {
                qop = val;
            } else if ("realm".equalsIgnoreCase(key)) {
                realm = val;
            } else if ("nonce".equalsIgnoreCase(key)) {
                nonce = val;
            } else if ("opaque".equalsIgnoreCase(key)) {
                opaque = val;
            } else if ("stale".equalsIgnoreCase(key)) {
                stale = Boolean.parseBoolean(val);
            } else if ("algorithm".equalsIgnoreCase(key)) {
                algorithm = val;
            } else if ("userhash".equalsIgnoreCase(key)) {
                userhash = Boolean.parseBoolean(val);
            }
        }
        final Algorithm parsed = Algorithm.parse(algorithm);
        if (parsed == null) {
            return null;
        }
        return new DigestScheme(realm, qop, nonce, opaque, parsed, stale, userhash);
    }

    private static Map<String, String> parse(final String v) {
        final Map<String, String> properties = new LinkedHashMap<>();
        final StringBuilder key = new StringBuilder();
        final StringBuilder value = new StringBuilder();
        boolean inKey = true;
        boolean inQuotes = false;
        for (char c : v.toCharArray()) {
            switch (c) {
                case '=': {
                    if (inKey) {
                        inKey = false;
                    }
                    if (inQuotes) {
                        value.append(c);
                    }
                    break;
                }
                case '"': {
                    if (inKey) {
                        throw new RuntimeException();
                    }
                    inQuotes = !inQuotes;
                    break;
                }
                case ',': {
                    if (inQuotes) {
                        value.append(c);
                    } else if (inKey) {
                        throw new RuntimeException();
                    } else {
                        properties.put(key.toString(), value.toString());
                        inKey = true;
                        key.setLength(0);
                        value.setLength(0);
                    }
                    break;
                }
                case ' ':
                case '\r':
                case '\n':
                case '\t': {
                    if (inQuotes) {
                        value.append(c);
                    }
                    break;
                }
                default: {
                    if (inKey) {
                        key.append(c);
                    } else {
                        value.append(c);
                    }
                }
            }
        }
        if (key.length() > 0) {
            properties.put(key.toString(), value.toString());
        }
        return properties;
    }
}
