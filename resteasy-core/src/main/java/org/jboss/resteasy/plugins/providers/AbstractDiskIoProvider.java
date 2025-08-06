/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.plugins.providers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.Cleanable;
import org.jboss.resteasy.plugins.server.Cleanables;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.NoContent;

public abstract class AbstractDiskIoProvider<T> implements MessageBodyReader<T>, AsyncMessageBodyWriter<T> {
    private static final String PREFIX = "pfx";

    private static final String SUFFIX = "sfx";

    protected Path readFromStream(MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        Path downloadedFile = Files.createTempFile(PREFIX, SUFFIX);

        Cleanables cleanables = ResteasyContext.getContextData(Cleanables.class);
        if (cleanables != null) {
            cleanables.addCleanable(new PathHolder(downloadedFile));
        } else {
            LogMessages.LOGGER.temporaryFileCreated(downloadedFile.toString());
        }

        if (NoContent.isContentLengthZero(httpHeaders))
            return downloadedFile;

        try (OutputStream output = Files.newOutputStream(downloadedFile)) {
            ProviderHelper.writeTo(entityStream, output);
        }

        return downloadedFile;
    }

    protected void writeToStream(Path uploadFile,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
        HttpHeaders headers = ResteasyContext.getContextData(HttpHeaders.class);
        if (headers == null) {
            writeIt(uploadFile, entityStream);
            return;
        }
        String range = headers.getRequestHeaders().getFirst("Range");
        if (range == null) {
            writeIt(uploadFile, entityStream);
            return;
        }
        range = range.trim();
        int byteUnit = range.indexOf("bytes=");
        if (byteUnit < 0) {
            //must start with 'bytes'
            writeIt(uploadFile, entityStream);
            return;
        }
        range = range.substring("bytes=".length());
        if (range.indexOf(',') > -1) {
            // we don't support this
            writeIt(uploadFile, entityStream);
            return;
        }
        int separator = range.indexOf('-');
        if (separator < 0) {
            writeIt(uploadFile, entityStream);
            return;
        } else if (separator == 0) {
            long fileSize = getPathSize(uploadFile);
            long begin = Long.parseLong(range);
            if (fileSize + begin < 1) {
                writeIt(uploadFile, entityStream);
                return;
            }
            // Should File->Path be propagated all the way down here?
            throw new FileRangeException(mediaType, uploadFile.toFile(), fileSize + begin, fileSize - 1);
        } else {
            try {
                throw buildFileRangeException(uploadFile, mediaType, range, separator);
            } catch (NumberFormatException e) {
                writeIt(uploadFile, entityStream);
                return;
            }
        }
    }

    protected CompletionStage<Void> asyncWriteToStream(Path uploadFile,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        HttpHeaders headers = ResteasyContext.getContextData(HttpHeaders.class);
        if (headers == null) {
            return writeIt(uploadFile, entityStream);
        }
        String range = headers.getRequestHeaders().getFirst("Range");
        if (range == null) {
            return writeIt(uploadFile, entityStream);
        }
        range = range.trim();
        int byteUnit = range.indexOf("bytes=");
        if (byteUnit < 0) {
            //must start with 'bytes'
            return writeIt(uploadFile, entityStream);
        }
        range = range.substring("bytes=".length());
        if (range.indexOf(',') > -1) {
            // we don't support this
            return writeIt(uploadFile, entityStream);
        }
        int separator = range.indexOf('-');
        if (separator < 0) {
            return writeIt(uploadFile, entityStream);
        } else if (separator == 0) {
            long fileSize = getPathSize(uploadFile);
            long begin = Long.parseLong(range);
            if (fileSize + begin < 1) {
                return writeIt(uploadFile, entityStream);
            }
            return ProviderHelper
                    .completedException(new FileRangeException(mediaType, uploadFile.toFile(), fileSize + begin, fileSize - 1));
        } else {
            try {
                return ProviderHelper.completedException(buildFileRangeException(uploadFile, mediaType, range, separator));
            } catch (NumberFormatException e) {
                return writeIt(uploadFile, entityStream);
            }
        }
    }

    protected void writeIt(Path uploadFile, OutputStream entityStream) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(uploadFile))) {
            ProviderHelper.writeTo(inputStream, entityStream);
        }
    }

    protected CompletionStage<Void> writeIt(Path uploadFile, AsyncOutputStream entityStream) {
        try {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(uploadFile));
            return ProviderHelper.writeToAndCloseInput(inputStream, entityStream);
        } catch (IOException e) {
            return ProviderHelper.completedException(e);
        }

    }

    private FileRangeException buildFileRangeException(Path uploadFile, MediaType mediaType, String range, int separator) {
        long fileSize = getPathSize(uploadFile);
        long begin = Long.parseLong(range.substring(0, separator));
        if (begin >= fileSize) {
            throw new WebApplicationException(416);
        }
        long end;
        if (range.endsWith("-")) {
            end = fileSize - 1;
        } else {
            String substring = range.substring(separator + 1);
            end = Long.parseLong(substring);
        }
        return new FileRangeException(mediaType, uploadFile.toFile(), begin, end);
    }

    protected long getPathSize(Path uploadFile) {
        try {
            return Files.size(uploadFile);
        } catch (IOException e) {
            return -1;
        }
    }

    private record PathHolder(Path file) implements Cleanable {

        @Override
        public void clean() throws Exception {
            Files.deleteIfExists(file);
        }
    }
}
