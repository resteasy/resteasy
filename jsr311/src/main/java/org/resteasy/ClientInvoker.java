package org.resteasy;

import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.util.FindAnnotation;
import org.resteasy.util.MediaTypeHelper;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.UriParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ProviderFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
abstract public class ClientInvoker {
    protected ProviderFactory providerFactory;
    protected Method method;
    protected ParameterMarshaller[] params;
    protected UriBuilderImpl builder;
    protected Class declaring;
    protected MediaType accepts;

    public ClientInvoker(Class<?> declaring, Method method, ProviderFactory providerFactory) {
        this.declaring = declaring;
        this.method = method;
        this.providerFactory = providerFactory;
        params = new ParameterMarshaller[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class type = method.getParameterTypes()[i];
            Annotation[] annotations = method.getParameterAnnotations()[i];

            QueryParam query;
            HeaderParam header;
            MatrixParam matrix;
            UriParam uriParam;

            if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null) {
                params[i] = new QueryParamMarshaller(query.value());
            } else if ((header = FindAnnotation.findAnnotation(annotations, HeaderParam.class)) != null) {
                params[i] = new HeaderParamMarshaller(header.value());
            } else if ((uriParam = FindAnnotation.findAnnotation(annotations, UriParam.class)) != null) {
                params[i] = new UriParamMarshaller(uriParam.value());
            } else if ((matrix = FindAnnotation.findAnnotation(annotations, MatrixParam.class)) != null) {
                params[i] = new MatrixParamMarshaller(matrix.value());
            } else {
                MediaType mediaType = MediaTypeHelper.getConsumes(declaring, method);
                if (mediaType == null) mediaType = determineMediaType();
                params[i] = new MessageBodyParameterMarshaller(mediaType, type, providerFactory);
            }
        }
        accepts = MediaTypeHelper.getProduces(declaring, method);
    }

    public void setBaseUri(URI uri) {
        builder = new UriBuilderImpl();
        builder.uri(uri);
        builder.path(declaring);
        builder.path(method);
    }

    public MediaType determineMediaType() {
        throw new RuntimeException("You must define a @ConsumeMime type.  In the future we will");
    }

    public abstract Object invoke(Object[] args);

}