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

package dev.resteasy.embedded.server;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@MessageLogger(projectCode = "RESTEASY")
interface LogMessages {

    LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackageName());

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 900001, value = "Property %s is of type %s and expected to be of type %s")
    void invalidProperty(String propertyName, String foundType, String expectedType);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 900010, value = "Failed to stop the deployment manager for servlet %s")
    void failedToStopDeploymentManager(@Cause Throwable cause, String servletName);
}
