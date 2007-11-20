package org.resteasy;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import javax.ws.rs.UriParam;
import javax.ws.rs.core.HttpContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ProviderFactory;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceInvoker {

    private ResourceFactory factory;
    private ProviderFactory providerFactory;
    private Method method;
    private ParameterExtractor[] params;
    private MediaType[] produces;
    private MediaType[] consumes;
    private Set<String> httpMethods;
    private Map<Integer, String> uriParams = new HashMap<Integer, String>();

    public static <T> T findAnnotation(Annotation[] searchList, Class<T> annotation) {
        for (Annotation ann : searchList) {
            if (ann.annotationType().equals(annotation)) return (T) ann;
        }
        return null;
    }

    public ResourceInvoker(Class<?> clazz, Method method, ResourceFactory factory, ProviderFactory providerFactory, Set<String> httpMethods) {
        this.method = method;
        this.factory = factory;
        this.httpMethods = httpMethods;
        this.providerFactory = providerFactory;

        params = new ParameterExtractor[method.getParameterTypes().length];
        ProduceMime p = method.getAnnotation(ProduceMime.class);
        if (p == null) p = clazz.getAnnotation(ProduceMime.class);
        ConsumeMime c = method.getAnnotation(ConsumeMime.class);
        if (c == null) c = clazz.getAnnotation(ConsumeMime.class);

        if (p != null) {
            produces = new MediaType[p.value().length];
            int i = 0;
            for (String mediaType : p.value()) {
                produces[i++] = MediaType.parse(mediaType);
            }
        }
        if (c != null) {
            consumes = new MediaType[c.value().length];
            int i = 0;
            for (String mediaType : c.value()) {
                consumes[i++] = MediaType.parse(mediaType);
            }
        }


        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class type = method.getParameterTypes()[i];
            Annotation[] annotations = method.getParameterAnnotations()[i];

            DefaultValue defaultValue = findAnnotation(method.getParameterAnnotations()[i], DefaultValue.class);
            String defaultVal = null;
            if (defaultValue != null) defaultVal = defaultValue.value();

            QueryParam query;
            HeaderParam header;
            MatrixParam matrix;
            UriParam uriParam;

            if ((query = findAnnotation(annotations, QueryParam.class)) != null) {
                params[i] = new QueryParamExtractor(method, query.value(), type, defaultVal);
            } else if ((header = findAnnotation(annotations, HeaderParam.class)) != null) {
                params[i] = new HeaderParamExtractor(method, query.value(), type, defaultVal);
            } else if ((uriParam = findAnnotation(annotations, UriParam.class)) != null) {
                params[i] = new UriParamExtractor(method, query.value(), type, defaultVal);
            } else if ((matrix = findAnnotation(annotations, MatrixParam.class)) != null) {
                params[i] = new MatrixParamExtractor(method, matrix.value(), type, defaultVal);
            } else if (findAnnotation(annotations, HttpContext.class) != null) {
                params[i] = new HttpContextParameter(type);
            } else {
                params[i] = new MessageBodyParameterExtractor(type, providerFactory);
            }
        }
    }

    public void addUriParam(int position, String paramName) {
        uriParams.put(position, paramName);
    }


    public void invoke(HttpInputMessage input, HttpOutputMessage output) {
        Object resource = factory.createResource();
        Object[] args = null;
        UriInfoImpl uriInfo = input.getUri();
        for (int i : uriParams.keySet()) {
            String paramName = uriParams.get(i);
            String value = uriInfo.getPathSegments().get(i).getPath();
            uriInfo.getQueryParameters().add(paramName, value);
        }
        if (params != null && params.length > 0) {
            args = new Object[params.length];
            int i = 0;
            for (ParameterExtractor extractor : params) {
                args[i++] = extractor.extract(input);
            }
        }
        try {
            Object rtn = method.invoke(resource, args);
            if (method.getReturnType().equals(void.class)) return;
            MediaType rtnType = matchByType(input.getHttpHeaders().getAcceptableMediaTypes());
            MessageBodyWriter writer = providerFactory.createMessageBodyWriter(method.getReturnType(), rtnType);
            try {
                long size = writer.getSize(rtn);
                output.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, ((Long) size).toString());
                writer.writeTo(rtn, rtnType, output.getOutputHeaders(), output.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }


    }

    public boolean matchByType(MediaType contentType, List<MediaType> accepts) {
        boolean matches = false;
        if (contentType == null) {
            matches = true;
        } else {
            if (consumes == null || consumes.length == 0)
            {
                matches = true;
            }
            else
            {
            for (MediaType type : consumes) {
                if (type.isCompatible(contentType)) {
                    matches = true;
                    break;
                }
            }
            }
        }
        if (!matches) return false;
        matches = false;
        if (accepts == null || accepts.size() == 0) return true;
        if (produces == null || produces.length == 0) return true;
        
        for (MediaType accept : accepts) {
            for (MediaType type : produces) {
                if (type.isCompatible(accept)) {
                    matches = true;
                    break;
                }
            }
        }
        return matches;
    }

    public MediaType matchByType(List<MediaType> accepts) {
        if (accepts == null || accepts.size() == 0) return produces[0];

        for (MediaType accept : accepts) {
            for (MediaType type : produces) {
                if (type.isCompatible(accept)) return type;
            }
        }
        return null;
    }

    public Set<String> getHttpMethods() {
        return httpMethods;
    }
}
