package org.jboss.resteasy.test.providers.custom.resource;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@Provider
@ApplicationPath("/")
public class SingletonCustomProviderApplication extends Application {

    public static Set<Class<?>> classes = new HashSet<Class<?>>();
    public static Set<Object> singletons = new HashSet<Object>();

    /**
     * @see Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        if (classes.isEmpty()) {
            classes.add(SingletonCustomProviderResource.class);
        }
        return classes;
    }

    /**
     * @see Application#getSingletons()
     */
    @Override
    public Set<Object> getSingletons() {
        if (singletons.isEmpty()) {
            singletons.add(new MessageBodyReader<SingletonCustomProviderObject>() {
                public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediType) {
                    return true;
                }

                public SingletonCustomProviderObject readFrom(Class<SingletonCustomProviderObject> type, Type genericType, Annotation[] annotations,
                                                              MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                        throws IOException, WebApplicationException {
                    throw new WebApplicationException(999); // deliberate crazy status
                }

            });
            singletons.add(new MessageBodyWriter<SingletonCustomProviderObject>() {
                public long getSize(SingletonCustomProviderObject dummyObject, Class<?> type, Type genericType, Annotation[] annotations,
                                    MediaType mediaType) {
                    return -1;
                }

                public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    return true;
                }

                public void writeTo(SingletonCustomProviderObject t, Class<?> type, Type genericType, Annotation[] annotations,
                                    MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                        throws IOException, WebApplicationException {
                    throw new WebApplicationException(999); // deliberate crazy status
                }
            });
        }
        return singletons;
    }
}
