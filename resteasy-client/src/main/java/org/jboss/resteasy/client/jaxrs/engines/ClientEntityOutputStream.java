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

package org.jboss.resteasy.client.jaxrs.engines;

import java.nio.file.Path;
import java.util.function.Supplier;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.EntityOutputStream;
import org.jboss.resteasy.spi.ResourceCleaner;
import org.jboss.resteasy.spi.config.Threshold;

/**
 * A stream used for entities in a client. This may buffer, given the threshold value, in memory or be written to a file.
 * The file should be deleted once this stream is no longer referenced.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ClientEntityOutputStream extends EntityOutputStream {

    /**
     * Creates a new entity stream with the maximum in memory threshold and a file prefix to be used if the stream needs
     * to be written to a file due to the threshold.
     *
     * @param threshold  the maximum threshold of memory
     * @param tmpDir     the temporary directory used for files, can be {@code null} to use the default
     * @param filePrefix the file prefix if a file is created
     */
    ClientEntityOutputStream(final Threshold threshold, final Path tmpDir, final Supplier<String> filePrefix) {
        super(threshold, tmpDir, filePrefix);
    }

    /**
     * Creates an entity for the Apache HTTP Client based on the output stream.
     *
     * @return an entity for the output stream
     */
    AbstractHttpEntity toEntity() {
        if (!isClosed()) {
            throw Messages.MESSAGES.streamNotClosed(this);
        }
        checkExported(Messages.MESSAGES.alreadyExported());
        synchronized (lock) {
            final Path file = getFile();
            if (file != null) {
                final AbstractHttpEntity result = new FileEntity(file.toFile());
                ResourceCleaner.register(result, new FileCleaner(file));
                return result;
            }
            return new ByteArrayEntity(getAndClearMemory());
        }
    }
}