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

package dev.resteasy.client.util.authentication;

import dev.resteasy.client.util.common.Bytes;

/**
 * Represents the credentials a user and password for authentication purposes.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface UserCredentials {

    /**
     * The username used for authentication.
     *
     * @return the username
     */
    String getUsername();

    /**
     * A byte array which represents the password for authentication.
     *
     * @return the password
     */
    byte[] getPassword();

    /**
     * Creates a user credential for the username and password provided.
     *
     * @param username the username used for authentication
     * @param password the password used for authentication
     *
     * @return the user credentials
     */
    static UserCredentials clear(final String username, final char[] password) {
        final byte[] p = Bytes.charToBytes(password);
        return new UserCredentials() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public byte[] getPassword() {
                return p.clone();
            }
        };
    }

    /**
     * Creates a user credential for the username and password provided.
     *
     * @param username the username used for authentication
     * @param password the password used for authentication
     *
     * @return the user credentials
     */
    static UserCredentials of(final String username, final byte[] password) {
        final byte[] clone = password.clone();
        return new UserCredentials() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public byte[] getPassword() {
                return clone.clone();
            }
        };
    }
}
