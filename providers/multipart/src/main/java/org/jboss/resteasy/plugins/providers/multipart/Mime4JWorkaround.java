/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.jboss.resteasy.plugins.providers.multipart;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.field.DefaultFieldParser;
import org.apache.james.mime4j.field.LenientFieldParser;
import org.apache.james.mime4j.message.BodyFactory;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.storage.AbstractStorageProvider;
import org.apache.james.mime4j.storage.DefaultStorageProvider;
import org.apache.james.mime4j.storage.Storage;
import org.apache.james.mime4j.storage.StorageBodyFactory;
import org.apache.james.mime4j.storage.StorageOutputStream;
import org.apache.james.mime4j.storage.StorageProvider;
import org.apache.james.mime4j.storage.ThresholdStorageProvider;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.jboss.resteasy.plugins.providers.multipart.i18n.LogMessages;

/**
 * Copy code from org.apache.james.mime4j.message.DefaultMessageBuilder.parseMessage().
 * Alter said code to use Mime4JWorkaroundBinaryEntityBuilder instead of EntityBuilder.
 */
class Mime4JWorkaround {
    /**
     * This is a rough copy of DefaultMessageBuilder.parseMessage() modified to use a Mime4JWorkaround as the contentHandler instead
     * of an EntityBuilder.
     * <p>
     *
     * @param is
     *
     * @return
     *
     * @throws IOException
     * @throws MimeIOException
     * @see org.apache.james.mime4j.message.DefaultMessageBuilder#parseMessage(java.io.InputStream)
     */
    static Message parseMessage(InputStream is) throws IOException, MimeIOException {
        try {
            MessageImpl message = new MessageImpl();
            MimeConfig cfg = MimeConfig.DEFAULT;
            boolean strict = cfg.isStrictParsing();
            DecodeMonitor mon = strict ? DecodeMonitor.STRICT : DecodeMonitor.SILENT;
            BodyDescriptorBuilder bdb = new DefaultBodyDescriptorBuilder(null, strict ? DefaultFieldParser.getParser() : LenientFieldParser.getParser(), mon);

            StorageProvider storageProvider;
            if (System.getProperty(DefaultStorageProvider.DEFAULT_STORAGE_PROVIDER_PROPERTY) != null) {
                storageProvider = DefaultStorageProvider.getInstance();
            } else {
                StorageProvider backend = new CustomTempFileStorageProvider();
                storageProvider = new ThresholdStorageProvider(backend, 1024);
            }
            BodyFactory bf = new StorageBodyFactory(storageProvider, mon);

            MimeStreamParser parser = new MimeStreamParser(cfg, mon, bdb);
            // EntityBuilder expect the parser will send ParserFields for the well known fields
            // It will throw exceptions, otherwise.
            parser.setContentHandler(new Mime4jWorkaroundBinaryEntityBuilder(message, bf));
            parser.setContentDecoding(false);
            parser.setRecurse();

            parser.parse(is);
            return message;
        } catch (MimeException e) {
            throw new MimeIOException(e);
        }
    }


    /**
     * A custom TempFileStorageProvider that do no set deleteOnExit on temp files,
     * to avoid memory leaks (see https://issues.apache.org/jira/browse/MIME4J-251)
     */
    private static class CustomTempFileStorageProvider extends AbstractStorageProvider {

        private static final String DEFAULT_PREFIX = "m4j";

        private final String prefix;

        private final String suffix;

        private final File directory;

        CustomTempFileStorageProvider() {
            this(DEFAULT_PREFIX, null, null);
        }

        CustomTempFileStorageProvider(final String prefix, final String suffix, final File directory) {
            if (prefix == null || prefix.length() < 3)
                throw new IllegalArgumentException("invalid prefix");

            if (directory != null && !directory.isDirectory() && !directory.mkdirs())
                throw new IllegalArgumentException("invalid directory");

            this.prefix = prefix;
            this.suffix = suffix;
            this.directory = directory;
        }

        public StorageOutputStream createStorageOutputStream() throws IOException {
            Path file;
            if (directory != null) {
                file = Files.createTempFile(directory.toPath(), prefix, suffix);
            } else {
                file = Files.createTempFile(prefix, suffix);
            }

            return new TempFileStorageOutputStream(file);
        }

        private static final class TempFileStorageOutputStream extends StorageOutputStream {
            private final Path file;

            private final OutputStream out;

            TempFileStorageOutputStream(final Path file) throws IOException {
                this.file = file;
                this.out = Files.newOutputStream(file);
            }

            @Override
            public void close() throws IOException {
                super.close();
                out.close();
            }

            @Override
            protected void write0(byte[] buffer, int offset, int length) throws IOException {
                out.write(buffer, offset, length);
            }

            @Override
            protected Storage toStorage0() throws IOException {
                // out has already been closed because toStorage calls close
                return new TempFileStorage(file);
            }
        }

        private static final class TempFileStorage implements Storage {

            private Path file;

            private static final Set<Path> filesToDelete = new HashSet<>();

            TempFileStorage(final Path file) {
                this.file = file;
            }

            public void delete() {
                // deleting a file might not immediately succeed if there are still
                // streams left open (especially under Windows). so we keep track of
                // the files that have to be deleted and try to delete all these
                // files each time this method gets invoked.

                // a better but more complicated solution would be to start a
                // separate thread that tries to delete the files periodically.

                synchronized (filesToDelete) {
                    if (file != null) {
                        filesToDelete.add(file);
                        file = null;
                    }

                    for (Iterator<Path> iterator = filesToDelete.iterator(); iterator.hasNext(); ) {
                        Path f = iterator.next();
                        try {
                            Files.deleteIfExists(f);
                            iterator.remove();
                        } catch (IOException e) {
                            LogMessages.LOGGER.debugf(e, "Failed to delete file %s", f);
                        }
                    }
                }
            }

            public InputStream getInputStream() throws IOException {
                if (file == null)
                    throw new IllegalStateException("storage has been deleted");

                return new BufferedInputStream(Files.newInputStream(file));
            }

        }
    }

}

