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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class Algorithm {
    private static final Logger LOGGER = Logger.getLogger(Algorithm.class.getPackageName());
    private final String algorithm;
    private final MessageDigest md;
    private final boolean isSession;

    private Algorithm(final String algorithm, final MessageDigest md, final boolean isSession) {
        this.algorithm = algorithm;
        this.md = md;
        this.isSession = isSession;
    }

    static Algorithm parse(final String algorithm) {
        String v = algorithm == null ? null : algorithm.toUpperCase(Locale.ROOT).trim();
        boolean session = false;
        if (v == null || v.isBlank()) {
            v = "MD5";
        } else {
            if (v.endsWith("-SESS")) {
                v = v.substring(0, v.indexOf("-SESS"));
                session = true;
            } else {
                v = algorithm;
            }
        }
        try {
            final MessageDigest md = MessageDigest.getInstance(v);
            return new Algorithm(algorithm, md, session);
        } catch (NoSuchAlgorithmException e) {
            // Java doesn't use the standard algorithm name for SHA-512-XXX, try converting this to a slash
            if (v.startsWith("SHA-512-")) {
                v = "SHA-512/" + v.substring(8);
            }
            try {
                final MessageDigest md = MessageDigest.getInstance(v);
                return new Algorithm(algorithm, md, session);
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.debugf(ex, "Failed to find digest for %s", algorithm);
            }
        }
        return null;
    }

    boolean isSession() {
        return isSession;
    }

    DigestHashBuilder builder() {
        return new DigestHashBuilder(md);
    }

    @Override
    public String toString() {
        return algorithm;
    }
}
