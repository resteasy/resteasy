package org.jboss.resteasy.plugins.providers.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.ref.Cleaner;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;

import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.field.ContentTypeField;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.stream.Field;
import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ProvidersContextRetainer;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResourceCleaner;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartInputImpl implements MultipartInput, ProvidersContextRetainer {
    private static class CleanupAction implements Runnable {
        private volatile Message mimeMessage;

        @Override
        public void run() {
            final Message message = mimeMessage;
            if (message != null) {
                message.dispose();
            }
        }
    }

    protected MediaType contentType;
    protected Providers workers;
    private final CleanupAction cleanupAction;
    private final Cleaner.Cleanable cleanable;
    protected List<InputPart> parts = new ArrayList<InputPart>();
    protected static final Annotation[] empty = {};
    protected MediaType defaultPartContentType = MultipartConstants.TEXT_PLAIN_WITH_CHARSET_US_ASCII_TYPE;
    protected String defaultPartCharset = null;
    protected Providers savedProviders;

    public MultipartInputImpl(final MediaType contentType, final Providers workers) {
        this.contentType = contentType;
        this.workers = workers;
        this.cleanupAction = new CleanupAction();
        cleanable = ResourceCleaner.register(this, cleanupAction);
        HttpRequest httpRequest = ResteasyContext.getContextData(HttpRequest.class);
        if (httpRequest != null) {
            String defaultContentType = (String) httpRequest
                    .getAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY);
            if (defaultContentType != null)
                this.defaultPartContentType = MediaType
                        .valueOf(defaultContentType);
            this.defaultPartCharset = (String) httpRequest.getAttribute(
                    InputPart.DEFAULT_CHARSET_PROPERTY);
            if (defaultPartCharset != null) {
                this.defaultPartContentType = getMediaTypeWithDefaultCharset(
                        this.defaultPartContentType);
            }
        }
    }

    public MultipartInputImpl(final MediaType contentType, final Providers workers,
            final MediaType defaultPartContentType,
            final String defaultPartCharset) {
        this.contentType = contentType;
        this.workers = workers;
        this.cleanupAction = new CleanupAction();
        cleanable = ResourceCleaner.register(this, cleanupAction);
        if (defaultPartContentType != null)
            this.defaultPartContentType = defaultPartContentType;
        this.defaultPartCharset = defaultPartCharset;
        if (defaultPartCharset != null) {
            this.defaultPartContentType = getMediaTypeWithDefaultCharset(this.defaultPartContentType);
        }
    }

    public MultipartInputImpl(final Multipart multipart, final Providers workers) throws IOException {
        for (Entity entity : multipart.getBodyParts()) {
            if (entity instanceof BodyPart) {
                parts.add(extractPart((BodyPart) entity));
            }
        }
        this.workers = workers;
        this.cleanupAction = new CleanupAction();
        cleanable = ResourceCleaner.register(this, cleanupAction);
    }

    public void parse(InputStream is) throws IOException {
        cleanupAction.mimeMessage = Mime4JWorkaround.parseMessage(addHeaderToHeadlessStream(is));
        extractParts();
    }

    protected InputStream addHeaderToHeadlessStream(InputStream is)
            throws UnsupportedEncodingException {
        return new SequenceInputStream(createHeaderInputStream(), is);
    }

    protected InputStream createHeaderInputStream()
            throws UnsupportedEncodingException {
        String header = HttpHeaders.CONTENT_TYPE + ": " + contentType
                + "\r\n\r\n";
        return new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8));
    }

    public String getPreamble() {
        return ((Multipart) getMimeMessage().getBody()).getPreamble();
    }

    public List<InputPart> getParts() {
        return parts;
    }

    protected void extractParts() throws IOException {
        Multipart multipart = (Multipart) getMimeMessage().getBody();
        for (Entity entity : multipart.getBodyParts()) {
            if (entity instanceof BodyPart) {
                parts.add(extractPart((BodyPart) entity));
            }
        }
    }

    protected InputPart extractPart(BodyPart bodyPart) throws IOException {
        return new PartImpl(bodyPart);
    }

    protected Message getMimeMessage() {
        return cleanupAction.mimeMessage;
    }

    public class PartImpl implements InputPart {

        private BodyPart bodyPart;
        private MediaType contentType;
        private MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();
        private boolean contentTypeFromMessage;

        public PartImpl(final BodyPart bodyPart) {
            this.bodyPart = bodyPart;
            for (Field field : bodyPart.getHeader()) {
                headers.add(field.getName(), field.getBody());
                if (field instanceof ContentTypeField) {
                    contentType = MediaType.valueOf(field.getBody());
                    contentTypeFromMessage = true;
                }
            }
            if (contentType == null)
                contentType = defaultPartContentType;
            if (getCharset(contentType) == null) {
                if (defaultPartCharset != null) {
                    contentType = getMediaTypeWithDefaultCharset(contentType);
                } else if (contentType.getType().equalsIgnoreCase("text")) {
                    contentType = getMediaTypeWithCharset(contentType, "us-ascii");
                }
            }
        }

        @Override
        public void setMediaType(MediaType mediaType) {
            contentType = mediaType;
            contentTypeFromMessage = false;
            headers.putSingle("Content-Type", mediaType.toString());
        }

        @SuppressWarnings("unchecked")
        public <T> T getBody(Class<T> type, Type genericType)
                throws IOException {
            boolean pushProviders = savedProviders != null && ResteasyContext.getContextData(Providers.class) == null;

            if (MultipartInput.class.isAssignableFrom(type)) {

                if (bodyPart.getBody() instanceof Multipart) {

                    if (MultipartInput.class.equals(type)) {
                        return (T) new MultipartInputImpl(
                                Multipart.class.cast(bodyPart.getBody()), workers);

                    } else if (MultipartRelatedInput.class.equals(type)) {
                        return (T) new MultipartRelatedInputImpl(
                                Multipart.class.cast(bodyPart.getBody()), workers);

                    } else if (MultipartFormDataInput.class.equals(bodyPart.getBody())) {
                        return (T) new MultipartFormDataInputImpl(
                                Multipart.class.cast(bodyPart.getBody()), workers);
                    }
                }
            }

            try {
                if (pushProviders) {
                    ResteasyContext.pushContext(Providers.class, savedProviders);
                }
                MessageBodyReader<T> reader = workers.getMessageBodyReader(type, genericType, empty, contentType);
                if (reader == null) {
                    throw new RuntimeException(Messages.MESSAGES.unableToFindMessageBodyReader(contentType, type.getName()));
                }

                LogMessages.LOGGER.debugf("MessageBodyReader: %s", reader.getClass().getName());

                return reader.readFrom(type, genericType, empty, contentType, headers, getBody());
            } finally {
                if (pushProviders) {
                    ResteasyContext.popContextData(Providers.class);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T getBody(GenericType<T> type) throws IOException {
            return getBody((Class<T>) type.getRawType(), type.getType());
        }

        @Override
        public InputStream getBody() throws IOException {
            Body body = bodyPart.getBody();
            InputStream result = null;
            if (body instanceof TextBody) {
                throw new UnsupportedOperationException();
                /*
                 * InputStreamReader reader = (InputStreamReader)((TextBody) body).getReader();
                 * StringBuilder inputBuilder = new StringBuilder();
                 * char[] buffer = new char[1024];
                 * while (true) {
                 * int readCount = reader.read(buffer);
                 * if (readCount < 0) {
                 * break;
                 * }
                 * inputBuilder.append(buffer, 0, readCount);
                 * }
                 * String str = inputBuilder.toString();
                 * return new ByteArrayInputStream(str.getBytes(reader.getEncoding()));
                 */
            } else if (body instanceof BinaryBody) {
                return ((BinaryBody) body).getInputStream();
            }
            return result;
        }

        public String getBodyAsString() throws IOException {
            return getBody(String.class, null);
        }

        @Override
        public String getFileName() {
            return bodyPart.getFilename();
        }

        public MultivaluedMap<String, String> getHeaders() {
            return headers;
        }

        public MediaType getMediaType() {
            return contentType;
        }

        public boolean isContentTypeFromMessage() {
            return contentTypeFromMessage;
        }
    }

    public static void main(String[] args) throws Exception {
        String input = "URLSTR: file:/Users/billburke/jboss/resteasy-jaxrs/resteasy-jaxrs/src/test/test-data/data.txt\r\n"
                + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
                + "Content-Disposition: form-data; name=\"part1\"\r\n"
                + "Content-Type: text/plain; charset=US-ASCII\r\n"
                + "Content-Transfer-Encoding: 8bit\r\n"
                + "\r\n"
                + "This is Value 1\r\n"
                + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
                + "Content-Disposition: form-data; name=\"part2\"\r\n"
                + "Content-Type: text/plain; charset=US-ASCII\r\n"
                + "Content-Transfer-Encoding: 8bit\r\n"
                + "\r\n"
                + "This is Value 2\r\n"
                + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3\r\n"
                + "Content-Disposition: form-data; name=\"data.txt\"; filename=\"data.txt\"\r\n"
                + "Content-Type: application/octet-stream; charset=ISO-8859-1\r\n"
                + "Content-Transfer-Encoding: binary\r\n"
                + "\r\n"
                + "hello world\r\n" + "--B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3--";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("boundary", "B98hgCmKsQ-B5AUFnm2FnDRCgHPDE3");
        MediaType contentType = new MediaType("multipart", "form-data",
                parameters);
        MultipartInputImpl multipart = new MultipartInputImpl(contentType, null);
        multipart.parse(bais);

        Logger LOG = Logger.getLogger(MultipartInputImpl.class);
        LOG.info(multipart.getPreamble());
        LOG.info("**********");
        for (InputPart part : multipart.getParts()) {
            LOG.info("--");
            LOG.info("\"" + part.getBodyAsString() + "\"");
        }
        LOG.info("done");

    }

    @Override
    public void close() {
        cleanable.clean();
    }

    protected String getCharset(MediaType mediaType) {
        for (Iterator<String> it = mediaType.getParameters().keySet().iterator(); it.hasNext();) {
            String key = it.next();
            if ("charset".equalsIgnoreCase(key)) {
                return mediaType.getParameters().get(key);
            }
        }
        return null;
    }

    private MediaType getMediaTypeWithDefaultCharset(MediaType mediaType) {
        String charset = defaultPartCharset;
        return getMediaTypeWithCharset(mediaType, charset);
    }

    private MediaType getMediaTypeWithCharset(MediaType mediaType, String charset) {
        Map<String, String> params = mediaType.getParameters();
        Map<String, String> newParams = new LinkedHashMap<String, String>();
        newParams.put("charset", charset);
        for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            if (!"charset".equalsIgnoreCase(key)) {
                newParams.put(key, params.get(key));
            }
        }
        return new MediaType(mediaType.getType(), mediaType.getSubtype(), newParams);
    }

    @Override
    public void setProviders(Providers providers) {
        savedProviders = providers;
    }
}
