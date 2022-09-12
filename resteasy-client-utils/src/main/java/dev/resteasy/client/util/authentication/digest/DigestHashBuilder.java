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

import dev.resteasy.client.util.common.ByteStringBuilder;
import dev.resteasy.client.util.common.Bytes;

/**
 * A simple digest has builder.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DigestHashBuilder {
    private final MessageDigest md;
    private final ByteStringBuilder builder;
    private boolean prependColon;

    /**
     * Creates a new hash builder based on the message digest.
     *
     * @param md the message digest to use for the hashing
     */
    DigestHashBuilder(final MessageDigest md) {
        this.md = md;
        builder = new ByteStringBuilder(256);
        prependColon = false;
    }

    /**
     * Adds the value to be hashed.
     *
     * @param value the value
     *
     * @return this builder
     */
    DigestHashBuilder append(final String value) {
        prepend();
        builder.append(value);
        return this;
    }

    /**
     * Adds the value to be hashed.
     *
     * @param value the value
     *
     * @return this builder
     */
    DigestHashBuilder append(final byte[] value) {
        prepend();
        builder.append(value);
        return this;
    }

    /**
     * Adds the value to be hashed.
     *
     * @param value the value
     *
     * @return this builder
     */
    DigestHashBuilder append(final Object value) {
        return append(String.valueOf(value));
    }

    /**
     * Creates the hash based on the digest passed in.
     *
     * @return the hashed string
     */
    String build() {
        prependColon = false;
        md.update(builder.toArray(true));
        try {
            return Bytes.bytesToHexString(md.digest());
        } finally {
            md.reset();
        }
    }

    private void prepend() {
        if (prependColon) {
            builder.append(':');
        } else {
            prependColon = true;
        }
    }
}
