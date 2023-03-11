package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Stack;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.ClientURI;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.CookieParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.FormParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.HeaderParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.MessageBodyParameterProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.URIParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.MatrixParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.PathParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.QueryParamProcessor;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.jboss.resteasy.util.MediaTypeHelper;

public class ProcessorFactory {

    public static Object[] createProcessors(Class declaringClass, Method method, ClientConfiguration configuration) {
        return createProcessors(declaringClass, method, configuration, null);
    }

    public static Object[] createProcessors(Class declaringClass, Method method, ClientConfiguration configuration,
            MediaType defaultConsumes) {
        Object[] params = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            String parameterName = method.getParameters()[i].getName();
            Class<?> type = method.getParameterTypes()[i];
            Annotation[] annotations = method.getParameterAnnotations()[i];
            Type genericType = method.getGenericParameterTypes()[i];
            if (TypeVariable.class.isInstance(genericType) && declaringClass.isInterface()
                    && !declaringClass.equals(method.getDeclaringClass())) {
                genericType = getTypeArgument((TypeVariable) genericType, declaringClass, method.getDeclaringClass());
            }
            AccessibleObject target = method;
            params[i] = ProcessorFactory.createProcessor(declaringClass, parameterName, configuration, type, annotations,
                    genericType, target, defaultConsumes, false);
        }
        return params;
    }

    public static Object createProcessor(Class<?> declaring, String defaultParameterName,
            ClientConfiguration configuration, Class<?> type,
            Annotation[] annotations, Type genericType, AccessibleObject target,
            boolean ignoreBody) {
        return createProcessor(declaring, defaultParameterName, configuration, type, annotations, genericType, target, null,
                ignoreBody);
    }

    public static Object createProcessor(Class<?> declaring, String defaultParameterName,
            ClientConfiguration configuration, Class<?> type,
            Annotation[] annotations, Type genericType, AccessibleObject target, MediaType defaultConsumes,
            boolean ignoreBody) {
        Object processor = null;

        QueryParam query;
        org.jboss.resteasy.annotations.jaxrs.QueryParam queryParam2;
        HeaderParam header;
        org.jboss.resteasy.annotations.jaxrs.HeaderParam header2;
        MatrixParam matrix;
        org.jboss.resteasy.annotations.jaxrs.MatrixParam matrix2;
        PathParam uriParam;
        org.jboss.resteasy.annotations.jaxrs.PathParam uriParam2;
        CookieParam cookie;
        org.jboss.resteasy.annotations.jaxrs.CookieParam cookie2;
        FormParam formParam;
        org.jboss.resteasy.annotations.jaxrs.FormParam formParam2;
        // Form form;

        boolean isEncoded = FindAnnotation.findAnnotation(annotations,
                Encoded.class) != null;

        if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null) {
            processor = new QueryParamProcessor(query.value(), genericType, annotations, configuration);
        } else if ((queryParam2 = FindAnnotation.findAnnotation(annotations,
                org.jboss.resteasy.annotations.jaxrs.QueryParam.class)) != null) {
            processor = new QueryParamProcessor(getParamName(defaultParameterName, queryParam2.value()), genericType,
                    annotations, configuration);
        } else if ((header = FindAnnotation.findAnnotation(annotations,
                HeaderParam.class)) != null) {
            processor = new HeaderParamProcessor(header.value(), genericType, annotations, configuration);
        } else if ((header2 = FindAnnotation.findAnnotation(annotations,
                org.jboss.resteasy.annotations.jaxrs.HeaderParam.class)) != null) {
            processor = new HeaderParamProcessor(getParamName(defaultParameterName, header2.value()), genericType, annotations,
                    configuration);
        } else if ((cookie = FindAnnotation.findAnnotation(annotations,
                CookieParam.class)) != null) {
            processor = new CookieParamProcessor(cookie.value(), genericType, annotations);
        } else if ((cookie2 = FindAnnotation.findAnnotation(annotations,
                org.jboss.resteasy.annotations.jaxrs.CookieParam.class)) != null) {
            processor = new CookieParamProcessor(getParamName(defaultParameterName, cookie2.value()), genericType, annotations);
        } else if ((uriParam = FindAnnotation.findAnnotation(annotations,
                PathParam.class)) != null) {
            processor = new PathParamProcessor(uriParam.value(), isEncoded, genericType, annotations, configuration);
        } else if ((uriParam2 = FindAnnotation.findAnnotation(annotations,
                org.jboss.resteasy.annotations.jaxrs.PathParam.class)) != null) {
            processor = new PathParamProcessor(getParamName(defaultParameterName, uriParam2.value()), isEncoded, genericType,
                    annotations, configuration);
        } else if ((matrix = FindAnnotation.findAnnotation(annotations,
                MatrixParam.class)) != null) {
            processor = new MatrixParamProcessor(matrix.value(), genericType, annotations, configuration);
        } else if ((matrix2 = FindAnnotation.findAnnotation(annotations,
                org.jboss.resteasy.annotations.jaxrs.MatrixParam.class)) != null) {
            processor = new MatrixParamProcessor(getParamName(defaultParameterName, matrix2.value()), genericType, annotations,
                    configuration);
        } else if ((formParam = FindAnnotation.findAnnotation(annotations,
                FormParam.class)) != null) {
            processor = new FormParamProcessor(formParam.value(), genericType, annotations, configuration);
        } else if ((formParam2 = FindAnnotation.findAnnotation(annotations,
                org.jboss.resteasy.annotations.jaxrs.FormParam.class)) != null) {
            processor = new FormParamProcessor(getParamName(defaultParameterName, formParam2.value()), genericType, annotations,
                    configuration);
        } else if ((/* form = */FindAnnotation.findAnnotation(annotations,
                Form.class)) != null) {
            processor = new FormProcessor(type, configuration, defaultParameterName);
        } else if ((/* form = */FindAnnotation.findAnnotation(annotations,
                BeanParam.class)) != null) {
            processor = new FormProcessor(type, configuration, defaultParameterName);
        } else if ((FindAnnotation.findAnnotation(annotations,
                Context.class)) != null) {
            processor = null;
        } else if (type.equals(Cookie.class)) {
            processor = new CookieParamProcessor(null);
        }
        // this is for HATEAOS clients
        else if (FindAnnotation.findAnnotation(annotations, ClientURI.class.getName()) != null) {
            processor = new URIParamProcessor();
        } else if (!ignoreBody) {
            MediaType mediaType = MediaTypeHelper.getConsumes(declaring, target);
            if (mediaType == null)
                mediaType = defaultConsumes;
            if (mediaType == null) {
                throw new RuntimeException(Messages.MESSAGES.mustDefineConsumesType());
            }
            processor = new MessageBodyParameterProcessor(mediaType, type,
                    genericType, annotations);
        }
        return processor;
    }

    private static String getParamName(String defaultName, String parameterValue) {
        return (parameterValue != null && parameterValue.length() > 0) ? parameterValue : defaultName;
    }

    static Type getTypeArgument(TypeVariable<?> var, Class<?> clazz, Class<?> baseInterface) {
        TypeVariable<?> tv = var;
        // collect superinterfaces
        Stack<Type> superinterfaces = new Stack<Type>();
        Type currentType;
        Class<?> currentClass = clazz;
        recursivePush(currentClass, baseInterface, superinterfaces);

        while (!superinterfaces.isEmpty()) {
            currentType = superinterfaces.pop();

            if (currentType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) currentType;
                Class<?> rawType = (Class) pt.getRawType();
                int argIndex = Arrays.asList(rawType.getTypeParameters()).indexOf(tv);
                if (argIndex > -1) {
                    Type typeArg = pt.getActualTypeArguments()[argIndex];
                    if (typeArg instanceof TypeVariable) {
                        // type argument is another type variable - look for the value of that
                        // variable in subclasses
                        tv = (TypeVariable<?>) typeArg;
                        continue;
                    } else {
                        // found the value - return it
                        return typeArg;
                    }
                }
            }

            // needed type argument not supplied - break and throw exception
            break;
        }
        throw new IllegalArgumentException(Messages.MESSAGES.doesNotSpecifyTypeParameter(var));
    }

    static void recursivePush(Type t, Class<?> baseInterface, Stack<Type> superinterfaces) {
        Class<?> currentClass = null;
        if (t instanceof Class) {
            currentClass = (Class) t;
        } else if (t instanceof ParameterizedType) {
            currentClass = (Class) ((ParameterizedType) t).getRawType();
        }
        if (baseInterface.isAssignableFrom(currentClass)) {
            superinterfaces.push(t);

            for (Type otherType : currentClass.getGenericInterfaces()) {
                recursivePush(otherType, baseInterface, superinterfaces);
            }
        }

    }
}
