package org.jboss.resteasy.spi.metadata;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.util.Types;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;

import javax.ws.rs.FormParam;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.jboss.resteasy.spi.util.FindAnnotation.findAnnotation;

public class SpringResourceBuilder extends ResourceBuilder {

    @Override
    public Class<? extends Annotation> getCorrespondingRootAnnotation() {
        return RestController.class;
    }

    protected ResourceClassBuilder createResourceClassBuilder(Class<?> clazz) {
        // classes can only be annotated with RequestMapping, not the meta-annotated friends
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            final String[] paths = getPath(requestMapping);
            if (paths != null && paths.length > 0) {
                // we only use the first path
                return new SpringResourceClassBuilder(clazz, paths[0]);
            }
        }

        return new SpringResourceClassBuilder(clazz, "/");
    }

    @Override
    protected void processMethod(boolean isLocator, ResourceClassBuilder resourceClassBuilder, Class<?> root,
                                 Method implementation)
    {
        Method method = getAnnotatedMethod(root, implementation);
        if (method == null) {
            return;
        }
        Set<String> httpMethods = getHttpMethods(method);
        if (httpMethods != null)
        {
            ResourceMethodBuilder resourceMethodBuilder = resourceClassBuilder.method(implementation, method);

            for (String httpMethod : httpMethods)
            {
                if (httpMethod.equalsIgnoreCase(RequestMethod.GET.name()))
                    resourceMethodBuilder.get();
                else if (httpMethod.equalsIgnoreCase(RequestMethod.PUT.name()))
                    resourceMethodBuilder.put();
                else if (httpMethod.equalsIgnoreCase(RequestMethod.POST.name()))
                    resourceMethodBuilder.post();
                else if (httpMethod.equalsIgnoreCase(RequestMethod.DELETE.name()))
                    resourceMethodBuilder.delete();
                else if (httpMethod.equalsIgnoreCase(RequestMethod.OPTIONS.name()))
                    resourceMethodBuilder.options();
                else if (httpMethod.equalsIgnoreCase(RequestMethod.HEAD.name()))
                    resourceMethodBuilder.head();
                else
                    resourceMethodBuilder.httpMethod(httpMethod);
            }
            handleProduces(resourceClassBuilder, method, resourceMethodBuilder);
            handleConsumes(resourceClassBuilder, method, resourceMethodBuilder);
            RequestMappingData methodRequestMapping = getRequestMapping(method);
            if (methodRequestMapping != null) {
                final String methodPath = methodRequestMapping.getFirstPath();
                if (methodPath != null) {
                    resourceMethodBuilder.path(replaceSpringWebWildcards(methodPath));
                }
            }

            for (int i = 0; i < resourceMethodBuilder.getLocator().getParams().length; i++)
            {
                resourceMethodBuilder.param(i).fromAnnotations();
            }
            resourceMethodBuilder.buildMethod();
        }
    }

    private String replaceSpringWebWildcards(String methodPath) {
        if (methodPath.contains("/**")) {
            methodPath = methodPath.replace("/**", "{unsetPlaceHolderVar:.*}");
        }
        if (methodPath.contains("/*")) {
            methodPath = methodPath.replace("/*", "/{unusedPlaceHolderVar}");
        }
        /*
         * Spring Web allows the use of '?' to capture a single character. We support this by
         * converting each url path using it to a JAX-RS syntax of variable followed by a regex.
         * So '/car?/s?o?/info' would become '/{notusedPlaceHolderVar:car.}/{notusedPlaceHolderVar:s.o.}/info'
         */
        String[] parts = methodPath.split("/");
        if (parts.length > 0) {
            StringBuilder sb = new StringBuilder(methodPath.startsWith("/") ? "/" : "");
            for (String part : parts) {
                if (part.isEmpty()) {
                    continue;
                }
                if (!sb.toString().endsWith("/")) {
                    sb.append("/");
                }
                if ((part.startsWith("{") && part.endsWith("}")) || !part.contains("?")) {
                    sb.append(part);
                } else {
                    sb.append("{notusedPlaceHolderVar:").append(part.replace('?', '.')).append("}");
                }
            }
            if (methodPath.endsWith("/")) {
                sb.append("/");
            }
            methodPath = sb.toString();
        }
        return methodPath;
    }

    private void handleConsumes(ResourceClassBuilder resourceClassBuilder, Method method, ResourceMethodBuilder resourceMethodBuilder) {
        final RequestMappingData requestMapping = getRequestMapping(resourceClassBuilder, method);
        if (requestMapping != null && requestMapping.getConsumes().length > 0)
            resourceMethodBuilder.consumes(requestMapping.getConsumes());
    }

    private void handleProduces(ResourceClassBuilder resourceClassBuilder, Method method, ResourceMethodBuilder resourceMethodBuilder) {
        final RequestMappingData requestMapping = getRequestMapping(resourceClassBuilder, method);
        if (requestMapping != null && requestMapping.getProduces().length > 0)
            resourceMethodBuilder.produces(requestMapping.getProduces());
        else {
            if (!String.class.equals(method.getReturnType()) && !void.class.equals(method.getReturnType())) {
                resourceMethodBuilder.produces(MediaType.APPLICATION_JSON_TYPE);
            }
        }
    }

    private RequestMappingData getRequestMapping(ResourceClassBuilder resourceClassBuilder, Method method) {
        RequestMappingData requestMapping = getRequestMapping(method);
        if (requestMapping == null)
            requestMapping = getRequestMapping(resourceClassBuilder.resourceClass.getClazz());
        if (requestMapping == null)
            requestMapping = getRequestMapping(method.getDeclaringClass());
        return requestMapping;
    }

    @Override
    public Method getAnnotatedMethod(final Class<?> root, final Method implementation)
    {
        if (implementation.isSynthetic())
        {
            return null;
        }

        if (getHttpMethods(implementation) != null)
        {
            return implementation;
        }

        // Check super-classes for inherited annotations
        for (Class<?> clazz = implementation.getDeclaringClass().getSuperclass(); clazz != null; clazz = clazz
                .getSuperclass())
        {
            final Method overriddenMethod = Types.findOverriddenMethod(implementation.getDeclaringClass(), clazz,
                    implementation);
            if (overriddenMethod == null)
            {
                continue;
            }

            if (getHttpMethods(overriddenMethod) != null)
            {
                return overriddenMethod;
            }
        }

        // Check implemented interfaces for inherited annotations
        for (Class<?> clazz = root; clazz != null; clazz = clazz.getSuperclass())
        {
            Method overriddenMethod = null;

            for (Class<?> classInterface : clazz.getInterfaces())
            {
                final Method overriddenInterfaceMethod = Types.getImplementedInterfaceMethod(root, classInterface,
                        implementation);
                if (overriddenInterfaceMethod == null)
                {
                    continue;
                }
                if (getHttpMethods(overriddenInterfaceMethod) == null)
                {
                    continue;
                }
                // Ensure no redefinition by peer interfaces (ambiguous) to preserve logic found in
                // original implementation
                if (overriddenMethod != null && !overriddenInterfaceMethod.equals(overriddenMethod))
                {
                    throw new RuntimeException(Messages.MESSAGES.ambiguousInheritedAnnotations(implementation));
                }

                overriddenMethod = overriddenInterfaceMethod;
            }

            if (overriddenMethod != null)
            {
                return overriddenMethod;
            }
        }

        return null;
    }

    private static Set<String> getHttpMethods(Method method)
    {

        final RequestMappingData requestMapping = getRequestMapping(method);
        if (requestMapping == null) {
            return null;
        }

        final Set<String> methods = new HashSet<>();
        // in Spring, when there is no method value specified for RequestMapping, the Java method is assumed to handle
        // all HTTP methods
        final RequestMethod[] methodsFromAnnotation = requestMapping.getMethod().length == 0 ? RequestMethod.values() : requestMapping.getMethod();
        for (RequestMethod requestMethod : methodsFromAnnotation) {
            methods.add(requestMethod.name());
        }

        return methods;
    }

    private static RequestMappingData getRequestMapping(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return RequestMappingData.fromRequestMapping(requestMapping);
        }
        for (Annotation annotation : method.getAnnotations())
        {
            // use the first declared annotation meta-annotated with RequestMapping since a single Java method
            // should only one meta-annotated RequestMapping annotation
            requestMapping = annotation.annotationType().getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                return RequestMappingData.fromAnnotation(annotation);
            }
        }
        return null;
    }

    private static RequestMappingData getRequestMapping(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return RequestMappingData.fromRequestMapping(requestMapping);
        }
        for (Annotation annotation : clazz.getAnnotations())
        {
            // use the first declared annotation meta-annotated with RequestMapping since a single Java method
            // should only one meta-annotated RequestMapping annotation
            requestMapping = annotation.annotationType().getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                return RequestMappingData.fromAnnotation(annotation);
            }
        }
        return null;
    }

    private static String[] getPath(RequestMapping requestMapping) {
        return getPath(requestMapping.path(), requestMapping.value());
    }

    private static String[] getPath(String[] path, String[] value) {
        if (path.length > 0) {
            return path;
        }
        return value;
    }

    private static class RequestMappingData {

        private final String[] path;
        private final RequestMethod[] method;
        private final String[] params;
        private final String[] headers;
        private final String[] consumes;
        private final String[] produces;

        private RequestMappingData(final String[] path, final RequestMethod[] method, final String[] params,
                                   final String[] headers, final String[] consumes, final String[] produces) {
            this.path = path;
            this.method = method;
            this.params = params;
            this.headers = headers;
            this.consumes = consumes;
            this.produces = produces;
        }

        public String[] getPath() {
            return path;
        }

        public RequestMethod[] getMethod() {
            return method;
        }

        public String[] getParams() {
            return params;
        }

        public String[] getHeaders() {
            return headers;
        }

        public String[] getConsumes() {
            return consumes;
        }

        public String[] getProduces() {
            return produces;
        }

        public String getFirstPath() {
            if (path != null && path.length > 0) {
                return path[0];
            }
            return null;
        }

        public static RequestMappingData fromRequestMapping(RequestMapping requestMapping) {
            return new RequestMappingData(
                    SpringResourceBuilder.getPath(requestMapping), requestMapping.method(), requestMapping.params(), requestMapping.headers(),
                    requestMapping.consumes(), requestMapping.produces());
        }

        public static RequestMappingData fromAnnotation(Annotation annotation) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getAnnotation(RequestMapping.class) == null) {
                return null;
            }

            // just use the same code over and over instead of doing reflection on the annotations themselves
            if (GetMapping.class.equals(annotationType)) {
                GetMapping mapping = (GetMapping) annotation;
                return new RequestMappingData(
                        SpringResourceBuilder.getPath(mapping.path(), mapping.value()), new RequestMethod[]{RequestMethod.GET}, mapping.params(), mapping.headers(),
                        mapping.consumes(), mapping.produces());
            }
            if (PostMapping.class.equals(annotationType)) {
                PostMapping mapping = (PostMapping) annotation;
                return new RequestMappingData(
                        SpringResourceBuilder.getPath(mapping.path(), mapping.value()), new RequestMethod[]{RequestMethod.POST}, mapping.params(), mapping.headers(),
                        mapping.consumes(), mapping.produces());
            }
            if (PutMapping.class.equals(annotationType)) {
                PutMapping mapping = (PutMapping) annotation;
                return new RequestMappingData(
                        SpringResourceBuilder.getPath(mapping.path(), mapping.value()), new RequestMethod[]{RequestMethod.PUT}, mapping.params(), mapping.headers(),
                        mapping.consumes(), mapping.produces());
            }
            if (DeleteMapping.class.equals(annotationType)) {
                DeleteMapping mapping = (DeleteMapping) annotation;
                return new RequestMappingData(
                        SpringResourceBuilder.getPath(mapping.path(), mapping.value()), new RequestMethod[]{RequestMethod.DELETE}, mapping.params(), mapping.headers(),
                        mapping.consumes(), mapping.produces());
            }
            if (PatchMapping.class.equals(annotationType)) {
                PatchMapping mapping = (PatchMapping) annotation;
                return new RequestMappingData(
                        SpringResourceBuilder.getPath(mapping.path(), mapping.value()), new RequestMethod[]{RequestMethod.PATCH}, mapping.params(), mapping.headers(),
                        mapping.consumes(), mapping.produces());
            }
            return null;
        }
    }

    private static class SpringResourceClassBuilder extends ResourceClassBuilder {

        SpringResourceClassBuilder(final Class<?> root, final String path) {
            super(root, path);
        }

        @Override
        public ResourceMethodBuilder method(Method method, Method annotatedMethod) {
            return new SpringResourceMethodBuilder(this, method, annotatedMethod);
        }
    }

    private static class SpringResourceMethodBuilder extends ResourceMethodBuilder {

        SpringResourceMethodBuilder(final ResourceClassBuilder resourceClassBuilder, final Method method, final Method annotatedMethod) {
            super(resourceClassBuilder, method, annotatedMethod);
        }

        @Override
        public ResourceMethodParameterBuilder param(int i) {
            String defaultName = null;
            if (this.getMethod().getAnnotatedMethod().getParameters()[i].isNamePresent()) {
                defaultName = this.getMethod().getAnnotatedMethod().getParameters()[i].getName();
            }
            return new SpringResourceMethodParameterBuilder(this, getLocator().getParams()[i], defaultName);
        }
    }

    private static class SpringResourceMethodParameterBuilder extends ResourceMethodParameterBuilder {

        final String defaultName;

        SpringResourceMethodParameterBuilder(final ResourceMethodBuilder method, final MethodParameter param, final String defaultName) {
            super(method, param);
            this.defaultName = defaultName;
        }

        @Override
        protected void doFromAnnotations() {
            final Parameter parameter = getParameter();
            Annotation[] annotations = parameter.getAnnotations();

            RequestParam requestParam;
            RequestHeader header;
            MatrixVariable matrix;
            PathVariable uriParam;
            CookieValue cookie;
            FormParam formParam;
            Form form;
            Suspended suspended;

            if ((requestParam = findAnnotation(annotations, RequestParam.class)) != null)
            {
                // TODO Spring Web RequestParam for both query params and form params so this needs to be improved
                parameter.setParamType(Parameter.ParamType.QUERY_PARAM);
                parameter.setParamName(requestParam.name());
                if (parameter.getParamName().isEmpty() && !requestParam.value().isEmpty()) {
                    parameter.setParamName(requestParam.value());
                }
                if (!requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    parameter.setDefaultValue(requestParam.defaultValue());
                }
            }
            else if ((header = findAnnotation(annotations, RequestHeader.class)) != null)
            {
                parameter.setParamType(Parameter.ParamType.HEADER_PARAM);
                parameter.setParamName(header.name());
                if (parameter.getParamName().isEmpty() && !header.value().isEmpty()) {
                    parameter.setParamName(header.value());
                }
                if (!header.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    parameter.setDefaultValue(header.defaultValue());
                }
            }
            else if ((cookie = findAnnotation(annotations, CookieValue.class)) != null)
            {
                parameter.setParamType(Parameter.ParamType.COOKIE_PARAM);
                parameter.setParamName(cookie.name());
                if (parameter.getParamName().isEmpty() && !cookie.value().isEmpty()) {
                    parameter.setParamName(cookie.value());
                }
                if (!cookie.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    parameter.setDefaultValue(cookie.defaultValue());
                }
            }
            else if ((uriParam = findAnnotation(annotations, PathVariable.class)) != null)
            {
                parameter.setParamType(Parameter.ParamType.PATH_PARAM);
                parameter.setParamName(uriParam.name());
                if (parameter.getParamName().isEmpty() && !uriParam.value().isEmpty()) {
                    parameter.setParamName(uriParam.value());
                }
            }
            else if ((matrix = findAnnotation(annotations, MatrixVariable.class)) != null)
            {
                parameter.setParamType(Parameter.ParamType.MATRIX_PARAM);
                parameter.setParamName(matrix.name());
                if (parameter.getParamName().isEmpty() && !matrix.value().isEmpty()) {
                    parameter.setParamName(matrix.value());
                }
                if (!matrix.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    parameter.setDefaultValue(matrix.defaultValue());
                }
            }
            else if (findAnnotation(annotations, RequestBody.class) != null)
            {
                parameter.setParamType(Parameter.ParamType.MESSAGE_BODY);
            } else if (parameter.getType().getName().startsWith("javax.servlet.http")) {  // is this perhaps too aggressive?
                parameter.setParamType(Parameter.ParamType.CONTEXT);
            } else {
                parameter.setParamType(Parameter.ParamType.UNKNOWN);
            }
            if (parameter.getParamName().isEmpty() && (defaultName != null)){
                parameter.setParamName(defaultName);
            }
        }
    }

}
