/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import jakarta.activation.DataSource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.Cleanable;
import org.jboss.resteasy.plugins.server.Cleanables;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.NoContent;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Consumes("*/*")
@Produces("*/*")
public class DataSourceProvider extends AbstractEntityProvider<DataSource> {

    protected static class SequencedDataSource implements DataSource {
        private final byte[] byteBuffer;
        private final int byteBufferOffset;
        private final int byteBufferLength;
        private final Path tempFile;
        private final String type;

        protected SequencedDataSource(final byte[] byteBuffer, final int byteBufferOffset,
                final int byteBufferLength, final File tempFile, final String type) {
            this(byteBuffer, byteBufferOffset, byteBufferLength, tempFile.toPath(), type);
        }

        protected SequencedDataSource(final byte[] byteBuffer, final int byteBufferOffset,
                final int byteBufferLength, final Path tempFile, final String type) {
            super();
            this.byteBuffer = byteBuffer;
            this.byteBufferOffset = byteBufferOffset;
            this.byteBufferLength = byteBufferLength;
            this.tempFile = tempFile;
            this.type = type;
        }

        @Override
        public String getContentType() {
            return type;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            InputStream bis = new ByteArrayInputStream(byteBuffer, byteBufferOffset, byteBufferLength);
            if (tempFile == null)
                return bis;
            return new SequenceInputStream(bis, Files.newInputStream(tempFile));
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException(Messages.MESSAGES.noOutputStreamAllowed());
        }

    }

    /**
     * @param in        input stream
     * @param mediaType media type
     * @return data source
     * @throws IOException if I/O error occurred
     */
    public static DataSource readDataSource(final InputStream in, final MediaType mediaType) throws IOException {
        byte[] memoryBuffer = new byte[4096];
        int readCount = in.read(memoryBuffer, 0, memoryBuffer.length);

        Path tempFile = null;
        if (readCount > 0) {
            byte[] buffer = new byte[4096];
            int count = in.read(buffer, 0, buffer.length);
            if (count > -1) {
                tempFile = Files.createTempFile("resteasy-provider-datasource", null);
                try (OutputStream fos = Files.newOutputStream(tempFile)) {
                    Cleanables cleanables = ResteasyContext.getContextData(Cleanables.class);
                    if (cleanables != null) {
                        cleanables.addCleanable(new TempFileCleanable(tempFile));
                    }
                    fos.write(buffer, 0, count);
                    ProviderHelper.writeTo(in, fos);
                }
            }
        }

        if (readCount == -1)
            readCount = 0;

        return new SequencedDataSource(memoryBuffer, 0, readCount, tempFile, mediaType.toString());
    }

    /**
     * Ascertain if the MessageBodyReader can produce an instance of a
     * particular type. The {@code type} parameter gives the
     * class of the instance that should be produced, the {@code genericType} parameter
     * gives the {@link java.lang.reflect.Type java.lang.reflect.Type} of the instance
     * that should be produced.
     * E.g. if the instance to be produced is {@code List<String>}, the {@code type} parameter
     * will be {@code java.util.List} and the {@code genericType} parameter will be
     * {@link java.lang.reflect.ParameterizedType java.lang.reflect.ParameterizedType}.
     *
     * @param type        the class of instance to be produced.
     * @param genericType the type of instance to be produced. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the formal type of the method parameter as returned by
     *                    {@code Method.getGenericParameterTypes}.
     * @param annotations an array of the annotations on the declaration of the
     *                    artifact that will be initialized with the produced instance. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the annotations on that parameter returned by
     *                    {@code Method.getParameterAnnotations}.
     * @param mediaType   the media type of the HTTP entity, if one is not
     *                    specified in the request then {@code application/octet-stream} is
     *                    used.
     * @return {@code true} if the type is supported, otherwise {@code false}.
     */
    @Override
    public boolean isReadable(Class<?> type,
            Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return DataSource.class.isAssignableFrom(type);
    }

    /**
     * Read a type from the {@link InputStream}.
     * <p>
     * In case the entity input stream is empty, the reader is expected to either return a
     * Java representation of a zero-length entity or throw a {@link jakarta.ws.rs.core.NoContentException}
     * in case no zero-length entity representation is defined for the supported Java type.
     * A {@code NoContentException}, if thrown by a message body reader while reading a server
     * request entity, is automatically translated by JAX-RS server runtime into a {@link jakarta.ws.rs.BadRequestException}
     * wrapping the original {@code NoContentException} and rethrown for a standard processing by
     * the registered {@link jakarta.ws.rs.ext.ExceptionMapper exception mappers}.
     * </p>
     *
     * @param type         the type that is to be read from the entity stream.
     * @param genericType  the type of instance to be produced. E.g. if the
     *                     message body is to be converted into a method parameter, this will be
     *                     the formal type of the method parameter as returned by
     *                     {@code Method.getGenericParameterTypes}.
     * @param annotations  an array of the annotations on the declaration of the
     *                     artifact that will be initialized with the produced instance. E.g.
     *                     if the message body is to be converted into a method parameter, this
     *                     will be the annotations on that parameter returned by
     *                     {@code Method.getParameterAnnotations}.
     * @param mediaType    the media type of the HTTP entity.
     * @param httpHeaders  the read-only HTTP headers associated with HTTP entity.
     * @param entityStream the {@link InputStream} of the HTTP entity. The
     *                     caller is responsible for ensuring that the input stream ends when the
     *                     entity has been consumed. The implementation should not close the input
     *                     stream.
     * @return the type that was read from the stream. In case the entity input stream is empty, the reader
     *         is expected to either return an instance representing a zero-length entity or throw
     *         a {@link jakarta.ws.rs.core.NoContentException} in case no zero-length entity representation is
     *         defined for the supported Java type.
     * @throws java.io.IOException                   if an IO error arises. In case the entity input stream is empty
     *                                               and the reader is not able to produce a Java representation for
     *                                               a zero-length entity, {@code NoContentException} is expected to
     *                                               be thrown.
     * @throws jakarta.ws.rs.WebApplicationException
     *                                               if a specific HTTP error response needs to be produced.
     *                                               Only effective if thrown prior to the response being committed.
     */
    @Override
    public DataSource readFrom(Class<DataSource> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        if (NoContent.isContentLengthZero(httpHeaders))
            return readDataSource(new ByteArrayInputStream(new byte[0]), mediaType);
        return readDataSource(entityStream, mediaType);
    }

    /**
     * Ascertain if the MessageBodyWriter supports a particular type.
     *
     * @param type        the class of instance that is to be written.
     * @param genericType the type of instance to be written, obtained either
     *                    by reflection of a resource method return type or via inspection
     *                    of the returned instance. {@link jakarta.ws.rs.core.GenericEntity}
     *                    provides a way to specify this information at runtime.
     * @param annotations an array of the annotations attached to the message entity instance.
     * @param mediaType   the media type of the HTTP entity.
     * @return {@code true} if the type is supported, otherwise {@code false}.
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return DataSource.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType);
    }

    /**
     * Write a type to an HTTP message. The message header map is mutable
     * but any changes must be made before writing to the output stream since
     * the headers will be flushed prior to writing the message body.
     *
     * @param dataSource   the instance to write.
     * @param type         the class of instance that is to be written.
     * @param genericType  the type of instance to be written. {@link jakarta.ws.rs.core.GenericEntity}
     *                     provides a way to specify this information at runtime.
     * @param annotations  an array of the annotations attached to the message entity instance.
     * @param mediaType    the media type of the HTTP entity.
     * @param httpHeaders  a mutable map of the HTTP message headers.
     * @param entityStream the {@link OutputStream} for the HTTP entity. The
     *                     implementation should not close the output stream.
     * @throws java.io.IOException                   if an IO error arises.
     * @throws jakarta.ws.rs.WebApplicationException
     *                                               if a specific HTTP error response needs to be produced.
     *                                               Only effective if thrown prior to the message being committed.
     */
    @Override
    public void writeTo(DataSource dataSource,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        InputStream in = dataSource.getInputStream();
        try {
            ProviderHelper.writeTo(in, entityStream);
        } finally {
            in.close();
        }

    }

    @Override
    public CompletionStage<Void> asyncWriteTo(DataSource dataSource, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        try {
            InputStream in = dataSource.getInputStream();
            return ProviderHelper.writeToAndCloseInput(in, entityStream);
        } catch (IOException e) {
            return ProviderHelper.completedException(e);
        }
    }

    private static class TempFileCleanable implements Cleanable {

        private final Path tempFile;

        TempFileCleanable(final Path tempFile) {
            this.tempFile = tempFile;
        }

        @Override
        public void clean() throws Exception {
            Files.deleteIfExists(tempFile);
        }
    }
}
