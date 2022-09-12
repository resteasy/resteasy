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

package org.jboss.resteasy.utils;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.ext.RuntimeDelegate;

public class CookieUtil {

    /**
     * Test utility to replace deprecated method {@code Cookie.valueOf(String)}.
     *
     * @param clazz the type of cookie you want to create
     * @param value the string value of the cookie
     *
     * @return the newly create cookie
     */
    public static <S extends Cookie> S valueOf(final Class<S> clazz, final String value) {
        return RuntimeDelegate.getInstance()
                .createHeaderDelegate(clazz)
                .fromString(value);
    }

    /**
     * Test utility to replace deprecated method {@code Cookie.toString()}.
     *
     * @param clazz the type of cookie you being passed in
     * @param value the cookie
     *
     * @return the string value of the cookie
     */
    public static <S extends Cookie> String toString(final Class<S> clazz, final S value) {
        return RuntimeDelegate.getInstance()
                .createHeaderDelegate(clazz)
                .toString(value);
    }
}
