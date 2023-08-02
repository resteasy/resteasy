package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.ext.ParamConverter;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractCollectionProcessor<T> {
    protected String paramName;
    protected Type type;
    protected Annotation[] annotations;
    protected ClientConfiguration config;

    public AbstractCollectionProcessor(final String paramName) {
        this.paramName = paramName;
    }

    public AbstractCollectionProcessor(final String paramName, final Type type, final Annotation[] annotations,
            final ClientConfiguration config) {
        this.paramName = paramName;
        this.type = type;
        this.annotations = annotations;
        this.config = config;
    }

    protected abstract T apply(T target, Object... objects);

    @SuppressWarnings("unchecked")
    public T buildIt(T target, Object object) {
        if (object == null)
            return target;
        if (object instanceof Collection) {
            if (annotations != null && type != null) {
                ParamConverter<Object> paramConverter = config.getParamConverter(object.getClass(), type, annotations);
                if (paramConverter != null) {
                    object = paramConverter.toString(object);
                    target = apply(target, object);
                } else {
                    target = apply(target, ((Collection<?>) object).toArray());
                }
            }
        } else if (object.getClass().isArray()) {
            ParamConverter<Object> paramConverter = config.getParamConverter(object.getClass(), type, annotations);
            if (paramConverter != null) {
                object = paramConverter.toString(object);
                target = apply(target, object);
            } else {
                Object[] arr = convertToObjectsArray(object);
                target = apply(target, arr);
            }
        } else {
            ParamConverter<Object> paramConverter = config.getParamConverter(object.getClass(), type, annotations);
            if (paramConverter != null) {
                object = paramConverter.toString(object);
            }
            target = apply(target, object);
        }
        return target;
    }

    private static Object[] convertToObjectsArray(Object array) {
        if (array instanceof Object[])
            return (Object[]) array;

        int length = Array.getLength(array);

        Object[] objects = new Object[length];
        for (int i = 0; i < length; i++) {
            objects[i] = Array.get(array, i);
        }

        return objects;
    }
}
