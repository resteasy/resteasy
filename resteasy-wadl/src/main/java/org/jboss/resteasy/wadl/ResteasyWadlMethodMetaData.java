package org.jboss.resteasy.wadl;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.wadl.i18n.LogMessages;
import org.jboss.resteasy.wadl.i18n.Messages;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlMethodMetaData {

    private ResourceMethodInvoker resourceInvoker;
    private Method method;
    private Class<?> klass;
    private List<String> produces;
    private List<String> consumesMIMETypes;
    private String uri;
    private String functionName;
    private List<ResteasyWadlMethodParamMetaData> parameters = new ArrayList<ResteasyWadlMethodParamMetaData>();
    private Collection<String> httpMethods;
    private ResteasyWadlServiceRegistry registry;
    private String functionPrefix;
    private boolean wantsForm;
    private String methodUri;
    private String klassUri;

    public String getMethodUri() {
        return methodUri;
    }

    public void setMethodUri(String methodUri) {
        this.methodUri = methodUri;
    }

    public String getKlassUri() {
        return klassUri;
    }

    public void setKlassUri(String klassUri) {
        this.klassUri = klassUri;
    }

    public ResteasyWadlMethodMetaData(ResteasyWadlServiceRegistry serviceRegistry, ResourceMethodInvoker resourceInvoker) {
        this.registry = serviceRegistry;
        this.resourceInvoker = resourceInvoker;
        this.method = resourceInvoker.getMethod();
        this.klass = resourceInvoker.getResourceClass();
        Path methodPath = method.getAnnotation(Path.class);
        methodUri = methodPath == null ? null : methodPath.value();
        Path klassPath = klass.getAnnotation(Path.class);
        klassUri = klassPath == null ? null : klassPath.value();

        Produces producesAnnotation = method.getAnnotation(Produces.class);
        if (producesAnnotation == null)
            producesAnnotation = klass.getAnnotation(Produces.class);
        this.produces = getProduces(producesAnnotation);
        Consumes consumes = method.getAnnotation(Consumes.class);
        if (consumes == null)
            consumes = klass.getAnnotation(Consumes.class);
        this.uri = appendURIFragments(registry, klassPath, methodPath);
        if (serviceRegistry.isRoot())
            this.functionPrefix = klass.getSimpleName();
        else
            this.functionPrefix = serviceRegistry.getFunctionPrefix();
        this.functionName = this.functionPrefix + "." + method.getName();
        httpMethods = resourceInvoker.getHttpMethods();

        // we need to add all parameters from parent resource locators until the root
        List<Method> methodsUntilRoot = new ArrayList<Method>();
        methodsUntilRoot.add(method);
        serviceRegistry.collectResourceMethodsUntilRoot(methodsUntilRoot);
        for (Method m : methodsUntilRoot) {
            Annotation[][] allAnnotations = m.getParameterAnnotations();
            Class<?>[] parameterTypes = m.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                processMetaData(parameterTypes[i], allAnnotations[i], true);
            }
        }
        // this must be after we scan the params in case of @Form
        this.consumesMIMETypes = getConsumes(consumes);
        if (wantsForm && !consumesMIMETypes.contains("application/x-www-form-urlencoded")) {
            LogMessages.LOGGER.warn(Messages.MESSAGES.overridingConsumesAnnotation());
            this.consumesMIMETypes = Arrays.asList("application/x-www-form-urlencoded");
        }
    }

    protected void processMetaData(Class<?> type, Annotation[] annotations,
                                   boolean useBody) {
        QueryParam queryParam;
        HeaderParam headerParam;
        MatrixParam matrixParam;
        PathParam pathParam;
        CookieParam cookieParam;
        FormParam formParam;
        Form form;

        // boolean isEncoded = FindAnnotation.findAnnotation(annotations,
        // Encoded.class) != null;

        if ((queryParam = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.QUERY_PARAMETER, queryParam
                    .value());
        } else if ((headerParam = FindAnnotation.findAnnotation(annotations,
                HeaderParam.class)) != null) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.HEADER_PARAMETER,
                    headerParam.value());
        } else if ((cookieParam = FindAnnotation.findAnnotation(annotations,
                CookieParam.class)) != null) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.COOKIE_PARAMETER,
                    cookieParam.value());
        } else if ((pathParam = FindAnnotation.findAnnotation(annotations,
                PathParam.class)) != null) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.PATH_PARAMETER,
                    pathParam.value());
        } else if ((matrixParam = FindAnnotation.findAnnotation(annotations,
                MatrixParam.class)) != null) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.MATRIX_PARAMETER,
                    matrixParam.value());
        } else if ((formParam = FindAnnotation.findAnnotation(annotations,
                FormParam.class)) != null) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.FORM_PARAMETER,
                    formParam.value());
            this.wantsForm = true;
        } else if ((form = FindAnnotation.findAnnotation(annotations, Form.class)) != null) {
            if (type == List.class) {
                addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.FORM, form.prefix());
                this.wantsForm = true;
            } else if (type == Map.class) {
                addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.FORM, form.prefix());
                this.wantsForm = true;
            } else
                walkForm(type);
        } else if ((FindAnnotation.findAnnotation(annotations, Context.class)) != null) {
            // righfully ignore
        } else if (useBody) {
            addParameter(type, annotations, ResteasyWadlMethodParamMetaData.MethodParamType.ENTITY_PARAMETER, null);
        }
    }

    private void walkForm(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            processMetaData(field.getType(), field.getAnnotations(), false);
        }
        for (Method method : type.getDeclaredMethods()) {
            if (method.getParameterTypes().length != 1
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
                              ResteasyWadlMethodParamMetaData.MethodParamType paramType, String value) {
        this.parameters.add(new ResteasyWadlMethodParamMetaData(type, annotations, paramType,
                value));
    }

    private List<String> getProduces(Produces produces) {
        if (produces == null)
            return new ArrayList<>();
        String[] values = produces.value();
        return Arrays.asList(values);
    }

    private List<String> getConsumes(Consumes consumes) {
        if (consumes == null)
            return Arrays.asList("text/plain");
        if (consumes.value().length > 0)
            return Arrays.asList(consumes.value());
        return Arrays.asList("text/plain");
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

    public ResourceMethodInvoker getResourceInvoker() {
        return resourceInvoker;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public List<String> getProduces() {
        return produces;
    }

    public List<String> getConsumesMIMETypes() {
        return consumesMIMETypes;
    }

    public String getUri() {
        return uri;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ResteasyWadlMethodParamMetaData> getParameters() {
        return parameters;
    }

    public Collection<String> getHttpMethods() {
        return httpMethods;
    }

    public static String appendURIFragments(ResteasyWadlServiceRegistry registry, Path classPath, Path methodPath) {
        return appendURIFragments(registry == null ? null : registry.getUri(),
                classPath != null ? classPath.value() : null,
                methodPath != null ? methodPath.value() : null);
    }

    public String getFunctionPrefix() {
        return functionPrefix;
    }
}
