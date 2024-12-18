/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.plugins.providers.jackson._private;

import java.lang.invoke.MethodHandles;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageLogger(projectCode = "RESTEASY-JACKSON")
public interface JacksonLogger {

    JacksonLogger LOGGER = Logger.getMessageLogger(MethodHandles.lookup(), JacksonLogger.class,
            "org.jboss.resteasy.plugins.providers.jackson");

    /**
     * Returns a message indicating the data could not be deserialized.
     *
     * @return a message indicating the data could not be deserialized
     */
    // Note we must use Message.NONE here. Otherwise, a prefix is added, and we do not want to expose where this is
    // coming from in the response.
    @Message(id = Message.NONE, value = "Not able to deserialize data provided.")
    String cannotDeserialize();

    /**
     * Logs a message indicating the data could not be deserialized.
     *
     * @param cause the cause of the error
     */
    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 100, value = "Not able to deserialize data provided")
    void logCannotDeserialize(@Cause Throwable cause);
}
