package org.jboss.resteasy.client.microprofile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Created by hbraun on 15.01.18.
 */
class MicroprofileClientBuilder implements RestClientBuilder {

    public MicroprofileClientBuilder() {
       ResteasyProviderFactory rpf = new MPResteasyProviderFactory();
       RegisterBuiltin.register(rpf);
       this.builderDelegate = new MPResteasyClientBuilder().providerFactory(rpf);
    }

    private final static String DEFAULT_MAPPER_PROP = "microprofile.rest.client.disable.default.mapper";

    @Override
    public RestClientBuilder baseUrl(URL url) {
        try {
            this.baseURI = url.toURI();
            return this;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public <T> T build(Class<T> aClass) throws IllegalStateException, RestClientDefinitionException {

        // Interface validity
        verifyInterface(aClass);

        // Provider annotations
        RegisterProvider[] providers = aClass.getAnnotationsByType(RegisterProvider.class);

        for (RegisterProvider provider : providers) {
            this.builderDelegate.register(provider.value(), provider.priority());
        }

        // Default exception mapper
        boolean defaultMapperDisabled = false;
        Object defaultMapperProp = this.builderDelegate.getConfiguration().getProperty(DEFAULT_MAPPER_PROP);
        if(defaultMapperProp!=null && defaultMapperProp.equals(Boolean.TRUE)) {
            defaultMapperDisabled = true;
        }

        if(!defaultMapperDisabled) {
            this.builderDelegate.register(DefaultResponseExceptionMapper.class);
        }

        ResteasyClient client = this.builderDelegate.build();
        return client
                .target(this.baseURI)
                .proxyBuilder(aClass)
                .defaultConsumes(MediaType.TEXT_PLAIN)
                .defaultProduces(MediaType.TEXT_PLAIN)
                .build();
    }

    private <T> void verifyInterface(Class<T> typeDef) {

        Method[] methods = typeDef.getMethods();

        // multiple verbs
        for (Method method : methods) {
            boolean hasHttpMethod = false;
            for (Annotation annotation : method.getAnnotations()) {
                boolean isHttpMethod = (annotation.annotationType().getAnnotation(HttpMethod.class) != null);
                if(!hasHttpMethod && isHttpMethod) {
                    hasHttpMethod = true;
                } else if(hasHttpMethod && isHttpMethod) {
                    throw new RestClientDefinitionException("Ambiguous @Httpmethod defintion on type " + typeDef);
                }
            }
        }

        // invalid parameter
        Path classPathAnno = typeDef.getAnnotation(Path.class);

        final Set<String> classLevelVariables = new HashSet<>();
        ResteasyUriBuilder classTemplate = null;
        if (classPathAnno != null) {
            classTemplate = (ResteasyUriBuilder)UriBuilder.fromUri(classPathAnno.value());
            classLevelVariables.addAll(classTemplate.getPathParamNamesInDeclarationOrder());
        }
        ResteasyUriBuilder template;
        for (Method method : methods) {

            Path methodPathAnno = method.getAnnotation(Path.class);
            if (methodPathAnno != null) {
                template = classPathAnno == null ? (ResteasyUriBuilder)UriBuilder.fromUri(methodPathAnno.value())
                        : (ResteasyUriBuilder)UriBuilder.fromUri(classPathAnno.value() + "/" + methodPathAnno.value());
            } else {
                template = classTemplate;
            }
            if (template == null) {
                continue;
            }

            // it's not executed, so this can be anything - but a hostname needs to present
            template.host("localhost");

            Set<String> allVariables = new HashSet<>(template.getPathParamNamesInDeclarationOrder());
            Map<String, Object> paramMap = new HashMap<>();
            for (Parameter p : method.getParameters()) {
                PathParam pathParam = p.getAnnotation(PathParam.class);
                if (pathParam != null) {
                    paramMap.put(pathParam.value(), "foobar");
                }
            }

            if(allVariables.size()!=paramMap.size()) {
                throw new RestClientDefinitionException("Parameters and variables don't match on "+ typeDef +"::"+method.getName());
            }

            try {
                template.resolveTemplates(paramMap, false).build();
            } catch (IllegalArgumentException ex) {
                throw new RestClientDefinitionException("Parameter names don't match variable names on "+ typeDef +"::"+method.getName(), ex);
            }

        }
    }


    @Override
    public Configuration getConfiguration() {
        return this.builderDelegate.getConfiguration();
    }

    @Override
    public RestClientBuilder property(String name, Object value) {
        this.builderDelegate.property(name, value);
        return this;
    }

    @Override
    public RestClientBuilder register(Class<?> aClass) {
        this.builderDelegate.register(aClass);
        return this;
    }

    @Override
    public RestClientBuilder register(Class<?> aClass, int i) {
        this.builderDelegate.register(aClass, i);
        return this;
    }

    @Override
    public RestClientBuilder register(Class<?> aClass, Class<?>[] classes) {
        this.builderDelegate.register(aClass, classes);
        return this;
    }

    @Override
    public RestClientBuilder register(Class<?> aClass, Map<Class<?>, Integer> map) {
        this.builderDelegate.register(aClass, map);
        return this;
    }

    @Override
    public RestClientBuilder register(Object o) {
        this.builderDelegate.register(o);
        return this;
    }

    @Override
    public RestClientBuilder register(Object o, int i) {
        this.builderDelegate.register(o, i);
        return this;
    }

    @Override
    public RestClientBuilder register(Object o, Class<?>[] classes) {
        this.builderDelegate.register(o, classes);
        return this;
    }

    @Override
    public RestClientBuilder register(Object o, Map<Class<?>, Integer> map) {
        this.builderDelegate.register(o, map);
        return this;
    }

    private final ResteasyClientBuilder builderDelegate;

    private URI baseURI;

}
