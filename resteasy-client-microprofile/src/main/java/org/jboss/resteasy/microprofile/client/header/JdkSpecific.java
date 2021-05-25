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

package org.jboss.resteasy.microprofile.client.header;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.microprofile.rest.client.RestClientDefinitionException;

/**
 * This is used for calls that are specific to different JVM versions.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class JdkSpecific {

    static MethodHandle createMethodHandle(final Method method, final Object clientProxy) {
        try {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            MethodHandles.Lookup lookup = constructor.newInstance(method.getDeclaringClass());
            return lookup
                    .in(method.getDeclaringClass())
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(clientProxy);
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            throw new RestClientDefinitionException("Failed to generate method handle for " + method, e);
        }
    }
}
