package org.jboss.resteasy.jsapi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.jsapi.MethodParamMetaData.MethodParamType;
import org.jboss.resteasy.jsapi.i18n.LogMessages;
import org.jboss.resteasy.jsapi.i18n.Messages;
import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.util.FindAnnotation;

public class MethodMetaData {

    private ResourceMethodInvoker invoker;
    private Method method;
    private ResourceMethod resourceMethod;
    private Class<?> klass;
    private String wants;
    private String consumesMIMEType;
    private String uri;
    private String functionName;
    private List<MethodParamMetaData> parameters = new ArrayList<MethodParamMetaData>();
    private Collection<String> httpMethods;
    private ServiceRegistry registry;
    private String functionPrefix;
    private boolean wantsForm;

    public MethodMetaData(final ServiceRegistry serviceRegistry, final ResourceMethodInvoker invoker) throws Exception {
        this.registry = serviceRegistry;
        this.invoker = invoker;
        this.method = invoker.getMethod();

        this.resourceMethod = (ResourceMethod) getResourceLocator(invoker);
        this.klass = invoker.getResourceClass();

        String methodPath = resourceMethod.getPath();
        String klassPath = resourceMethod.getResourceClass().getPath();
        Produces produces = method.getAnnotation(Produces.class);

        if (produces == null)
            produces = klass.getAnnotation(Produces.class);
        this.wants = getWants(produces);
        Consumes consumes = method.getAnnotation(Consumes.class);
        if (consumes == null)
            consumes = klass.getAnnotation(Consumes.class);

        this.uri = appendURIFragments(registry, klassPath, methodPath);

        if (serviceRegistry.isRoot())
            this.functionPrefix = klass.getSimpleName();
        else
            this.functionPrefix = serviceRegistry.getFunctionPrefix();
        this.functionName = this.functionPrefix + "." + method.getName();
        httpMethods = invoker.getHttpMethods();

        // we need to add all parameters from parent resource locators until the root
        List<Method> methodsUntilRoot = new ArrayList<Method>();
        methodsUntilRoot.add(method);
        serviceRegistry.collectResourceMethodsUntilRoot(methodsUntilRoot);
        for (Method method : methodsUntilRoot) {
            Annotation[][] allAnnotations = method.getParameterAnnotations();
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                processMetaData(parameterTypes[i], allAnnotations[i], true);
            }
        }
        // this must be after we scan the params in case of @Form
        this.consumesMIMEType = getConsumes(consumes);
        if (wantsForm && !"application/x-www-form-urlencoded".equals(consumesMIMEType)) {
            LogMessages.LOGGER.warn(Messages.MESSAGES.overridingConsumes());
            this.consumesMIMEType = "application/x-www-form-urlencoded";
        }
    }

    public static ResourceLocator getResourceLocator(ResourceInvoker invoker) throws Exception {
        Field resourceMethodField = null;
        resourceMethodField = invoker.getClass().getDeclaredField("method");
        resourceMethodField.setAccessible(true);
        return (ResourceLocator) resourceMethodField.get(invoker);
    }

    protected void processMetaData(Class<?> type, Annotation[] annotations,
            boolean useBody) {
        QueryParam query;
        HeaderParam header;
        MatrixParam matrix;
        PathParam uriParam;
        CookieParam cookie;
        FormParam formParam;
        Form form;

        // boolean isEncoded = FindAnnotation.findAnnotation(annotations,
        // Encoded.class) != null;

        if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null) {
            addParameter(type, annotations, MethodParamType.QUERY_PARAMETER, query
                    .value());
        } else if ((header = FindAnnotation.findAnnotation(annotations,
                HeaderParam.class)) != null) {
            addParameter(type, annotations, MethodParamType.HEADER_PARAMETER,
                    header.value());
        } else if ((cookie = FindAnnotation.findAnnotation(annotations,
                CookieParam.class)) != null) {
            addParameter(type, annotations, MethodParamType.COOKIE_PARAMETER,
                    cookie.value());
        } else if ((uriParam = FindAnnotation.findAnnotation(annotations,
                PathParam.class)) != null) {
            addParameter(type, annotations, MethodParamType.PATH_PARAMETER,
                    uriParam.value());
        } else if ((matrix = FindAnnotation.findAnnotation(annotations,
                MatrixParam.class)) != null) {
            addParameter(type, annotations, MethodParamType.MATRIX_PARAMETER,
                    matrix.value());
        } else if ((formParam = FindAnnotation.findAnnotation(annotations,
                FormParam.class)) != null) {
            addParameter(type, annotations, MethodParamType.FORM_PARAMETER,
                    formParam.value());
            this.wantsForm = true;
        } else if ((form = FindAnnotation.findAnnotation(annotations, Form.class)) != null) {
            if (type == Map.class || type == List.class) {
                addParameter(type, annotations, MethodParamType.FORM, form.prefix());
                this.wantsForm = true;
            } else
                walkForm(type);
        } else if ((FindAnnotation.findAnnotation(annotations, Context.class)) != null) {
            // righfully ignore
        } else if (useBody) {
            addParameter(type, annotations, MethodParamType.ENTITY_PARAMETER, null);
        }
    }

    private void walkForm(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            processMetaData(field.getType(), field.getAnnotations(), false);
        }
        for (Method method : type.getDeclaredMethods()) {
            if (method.getParameterCount() != 1
                    || !method.getReturnType().equals(Void.class))
                continue;
            processMetaData(method.getParameterTypes()[0],
                    method.getAnnotations(), false);
        }
        if (type.getSuperclass() != null) {
            walkForm(type.getSuperclass());
        }
    }

    private void addParameter(Class<?> type, Annotation[] annotations,
            MethodParamType paramType, String value) {
        this.parameters.add(new MethodParamMetaData(type, annotations, paramType,
                value));
    }

    private String getWants(Produces produces) {
        if (produces == null)
            return null;
        String[] value = produces.value();
        if (value.length == 0)
            return null;
        if (value.length == 1)
            return value[0];
        StringBuffer buf = new StringBuffer();
        for (String mime : produces.value()) {
            if (buf.length() != 0)
                buf.append(",");
            buf.append(mime);
        }
        return buf.toString();
    }

    private String getConsumes(Consumes consumes) {
        if (consumes == null)
            return "text/plain";
        if (consumes.value().length > 0)
            return consumes.value()[0];
        return "text/plain";
    }

    public static String appendURIFragments(ServiceRegistry registry, String classPath, String methodPath) {
        return appendURIFragments(registry == null ? null : registry.getUri(),
                notEmpty(classPath) ? classPath : null,
                notEmpty(methodPath) ? methodPath : null);
    }

    public static String appendURIFragments(String... fragments) {
        StringBuilder str = new StringBuilder();
        for (String fragment : fragments) {
            if (fragment == null || fragment.length() == 0 || fragment.equals("/"))
                continue;
            if (fragment.startsWith("/"))
                fragment = fragment.substring(1);
            if (fragment.endsWith("/"))
                fragment = fragment.substring(0, fragment.length() - 1);
            str.append('/').append(fragment);
        }
        if (str.length() == 0)
            return "/";
        return str.toString();
    }

    public ResourceMethodInvoker getInvoker() {
        return invoker;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public String getWants() {
        return wants;
    }

    public String getConsumesMIMEType() {
        return consumesMIMEType;
    }

    public String getUri() {
        return uri;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<MethodParamMetaData> getParameters() {
        return parameters;
    }

    public Collection<String> getHttpMethods() {
        return httpMethods;
    }

    private static boolean notEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public String getFunctionPrefix() {
        return functionPrefix;
    }
}
