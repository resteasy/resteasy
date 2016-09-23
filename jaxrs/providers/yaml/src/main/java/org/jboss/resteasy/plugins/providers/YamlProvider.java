package org.jboss.resteasy.plugins.providers;


import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.yaml.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.yaml.i18n.Messages;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.WriterException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * Provider for YAML &lt;-> Object marshalling. Uses the following mime
 * types:<pre><code>
 *   text/yaml
 *   text/x-yaml
 *   application/x-yaml</code></pre>
 *
 * @author Martin Algesten
 */
@Provider
@Consumes({"text/yaml", "text/x-yaml", "application/x-yaml"})
@Produces({"text/yaml", "text/x-yaml", "application/x-yaml"})
public class YamlProvider extends AbstractEntityProvider<Object> {

    // MessageBodyReader

    @LogMessage(level = Level.DEBUG)
    @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.YamlProvider , method call : isReadable .")
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @LogMessage(level = Level.DEBUG)
    @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.YamlProvider , method call : readFrom .")
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException {

        try {
            if (isValidInternalType(type)) {
                return new Yaml().load(entityStream);
            } else {
                CustomClassLoaderConstructor customClassLoaderConstructor = new CustomClassLoaderConstructor(type.getClassLoader());
                return new Yaml(customClassLoaderConstructor).loadAs(entityStream, type);
            }
        } catch (Exception e) {
            throw new ReaderException(Messages.MESSAGES.failedToDecodeYaml(), e);
        }
    }

    // MessageBodyWriter
    @LogMessage(level = Level.DEBUG)
    @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.YamlProvider , method call : isValidInternalType .")
    protected boolean isValidInternalType(Class type) {
        if (List.class.isAssignableFrom(type)
                || Set.class.isAssignableFrom(type)
                || Map.class.isAssignableFrom(type)
                || type.isArray()) {
            return true;
        } else {
            return false;
        }
    }

    @LogMessage(level = Level.DEBUG)
    @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.YamlProvider , method call : isValidType .")
    protected boolean isValidType(Class type) {

        if (isValidInternalType(type)) {
            return true;
        }
        if (StreamingOutput.class.isAssignableFrom(type)) return false;
        String className = type.getName();
        if (className.startsWith("java.")) return false;
        if (className.startsWith("javax.")) return false;
        if (type.isPrimitive()) return false;

        return true;
    }


    @LogMessage(level = Level.DEBUG)
    @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.YamlProvider , method call : isWriteable .")
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isValidType(type);
    }

    @LogMessage(level = Level.DEBUG)
    @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.YamlProvider , method call : writeTo .")
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
            WebApplicationException {

        try {
            entityStream.write(new Yaml().dump(t).getBytes());
        } catch (Exception e) {

            LogMessages.LOGGER.debug(Messages.MESSAGES.failedToEncodeYaml(t.toString()));
            throw new WriterException(e);

        }

    }

}
