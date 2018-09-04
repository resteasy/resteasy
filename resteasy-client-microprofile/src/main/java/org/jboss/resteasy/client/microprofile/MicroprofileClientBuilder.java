package org.jboss.resteasy.client.microprofile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ParamConverterProvider;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;

/**
 * Created by hbraun on 15.01.18.
 */
class MicroprofileClientBuilder implements RestClientBuilder {

   private static final String DEFAULT_MAPPER_PROP = "microprofile.rest.client.disable.default.mapper";
   private static final Logger LOG = Logger.getLogger(MicroprofileClientBuilder.class);

   MicroprofileClientBuilder() {
       ClientBuilder availableBuilder = ClientBuilder.newBuilder();

       if (availableBuilder instanceof ResteasyClientBuilder) {
           this.builderDelegate = (ResteasyClientBuilder) availableBuilder;
           this.configurationWrapper = new ConfigurationWrapper(this.builderDelegate.getConfiguration());
           Config cfg = null;
           try {
              ConfigProviderResolver.instance();
              cfg = ConfigProvider.getConfig();
           } catch (IllegalStateException ise) {
              //ignore
           }
           this.config = cfg;
       } else {
           throw new IllegalStateException("Incompatible client builder found " + availableBuilder.getClass());
       }
   }

   public Configuration getConfigurationWrapper() {
       return this.configurationWrapper;
   }

   @Override
   public RestClientBuilder baseUrl(URL url) {
       try {
           this.baseURI = url.toURI();
           return this;
       } catch (URISyntaxException e) {
           throw new RuntimeException(e.getMessage());
       }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T build(Class<T> aClass) throws IllegalStateException, RestClientDefinitionException {

       // Interface validity
       verifyInterface(aClass);

       // Provider annotations
       RegisterProvider[] providers = aClass.getAnnotationsByType(RegisterProvider.class);

       for (RegisterProvider provider : providers) {
           register(provider.value(), provider.priority());
       }

       // Default exception mapper
       if (!isMapperDisabled()) {
           register(DefaultResponseExceptionMapper.class);
       }

       this.builderDelegate.register(new ExceptionMapping(localProviderInstances), 1);

       ClassLoader classLoader = aClass.getClassLoader();

       List<String> noProxyHosts = Arrays.asList(
               System.getProperty("http.nonProxyHosts", "localhost|127.*|[::1]").split("|"));

       final T actualClient;

       final String proxyHost = System.getProperty("http.proxyHost");

       if (proxyHost != null && !noProxyHosts.contains(this.baseURI.getHost())) {
           // Use proxy, if defined
           actualClient = this.builderDelegate.defaultProxy(
                   proxyHost,
                   Integer.parseInt(System.getProperty("http.proxyPort", "80")))
                   .build()
                   .target(this.baseURI)
                   .proxyBuilder(aClass)
                   .classloader(classLoader)
                   .defaultConsumes(MediaType.TEXT_PLAIN)
                   .defaultProduces(MediaType.TEXT_PLAIN)
                   .build();
       } else {
           actualClient = this.builderDelegate.build()
                   .target(this.baseURI)
                   .proxyBuilder(aClass)
                   .classloader(classLoader)
                   .defaultConsumes(MediaType.TEXT_PLAIN)
                   .defaultProduces(MediaType.TEXT_PLAIN)
                   .build();
       }

       return (T) Proxy.newProxyInstance(
               classLoader,
               new Class[] {aClass},
               new ProxyInvocationHandler(actualClient, getLocalProviderInstances())
       );

   }

   private boolean isMapperDisabled() {
       boolean disabled = false;
       Optional<Boolean> defaultMapperProp = this.config != null ? this.config.getOptionalValue(DEFAULT_MAPPER_PROP, Boolean.class) : Optional.empty();

       // disabled through config api
       if (defaultMapperProp.isPresent() && defaultMapperProp.get().equals(Boolean.TRUE)) {
           disabled = true;
       } else if (!defaultMapperProp.isPresent()) {

           // disabled through jaxrs property
           try {
               Object property = this.builderDelegate.getConfiguration().getProperty(DEFAULT_MAPPER_PROP);
               if (property != null) {
                   disabled = (Boolean)property;
               }
           } catch (Throwable e) {
               // ignore cast exception
           }
       }
       return disabled;
   }

   private <T> void verifyInterface(Class<T> typeDef) {

       Method[] methods = typeDef.getMethods();

       // multiple verbs
       for (Method method : methods) {
           boolean hasHttpMethod = false;
           for (Annotation annotation : method.getAnnotations()) {
               boolean isHttpMethod = (annotation.annotationType().getAnnotation(HttpMethod.class) != null);
               if (!hasHttpMethod && isHttpMethod) {
                   hasHttpMethod = true;
               } else if (hasHttpMethod && isHttpMethod) {
                   throw new RestClientDefinitionException("Ambiguous @Httpmethod defintion on type " + typeDef);
               }
           }
       }

       // invalid parameter
       Path classPathAnno = typeDef.getAnnotation(Path.class);

       final Set<String> classLevelVariables = new HashSet<>();
       ResteasyUriBuilder classTemplate = null;
       if (classPathAnno != null) {
           classTemplate = (ResteasyUriBuilder) UriBuilder.fromUri(classPathAnno.value());
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

           if (allVariables.size() != paramMap.size()) {
               throw new RestClientDefinitionException("Parameters and variables don't match on " + typeDef + "::" + method.getName());
           }

           try {
               template.resolveTemplates(paramMap, false).build();
           } catch (IllegalArgumentException ex) {
               throw new RestClientDefinitionException("Parameter names don't match variable names on " + typeDef + "::" + method.getName(), ex);
           }

       }
   }


   @Override
   public Configuration getConfiguration() {
       return getConfigurationWrapper();
   }

   @Override
   public RestClientBuilder property(String name, Object value) {
       this.builderDelegate.property(name, value);
       return this;
   }

   private static Object newInstanceOf(Class clazz) {
       try {
           return clazz.newInstance();
       } catch (Throwable t) {
           throw new RuntimeException("Failed to register " + clazz, t);
       }
   }
   @Override
   public RestClientBuilder register(Class<?> aClass) {
       this.register(newInstanceOf(aClass));
       return this;
   }

   @Override
   public RestClientBuilder register(Class<?> aClass, int i) {

       this.register(newInstanceOf(aClass), i);
       return this;
   }

   @Override
   public RestClientBuilder register(Class<?> aClass, Class<?>[] classes) {
       this.register(newInstanceOf(aClass), classes);
       return this;
   }

   @Override
   public RestClientBuilder register(Class<?> aClass, Map<Class<?>, Integer> map) {
       this.register(newInstanceOf(aClass), map);
       return this;
   }

   @Override
   public RestClientBuilder register(Object o) {
       if (o instanceof ResponseExceptionMapper) {
           ResponseExceptionMapper mapper = (ResponseExceptionMapper)o;
           register(mapper, mapper.getPriority());
       } else if (o instanceof ParamConverterProvider) {
           register(o, Priorities.USER);
       } else {
           this.builderDelegate.register(o);
       }
       return this;
   }

   @Override
   public RestClientBuilder register(Object o, int i) {
       if (o instanceof ResponseExceptionMapper) {

           // local
           ResponseExceptionMapper mapper = (ResponseExceptionMapper)o;
           HashMap<Class<?>, Integer> contracts = new HashMap<>();
           contracts.put(ResponseExceptionMapper.class, i);
           registerLocalProviderInstance(mapper, contracts);

           // delegate
           this.builderDelegate.register(mapper, i);

       } else if (o instanceof ParamConverterProvider) {

           // local
           ParamConverterProvider converter = (ParamConverterProvider)o;
           HashMap<Class<?>, Integer> contracts = new HashMap<>();
           contracts.put(ParamConverterProvider.class, i);
           registerLocalProviderInstance(converter, contracts);

           // delegate
           this.builderDelegate.register(converter, i);

       } else {
           this.builderDelegate.register(o, i);
       }
       return this;
   }

   @Override
   public RestClientBuilder register(Object o, Class<?>[] classes) {

       // local
       for (Class<?> aClass : classes) {
           if (aClass.isAssignableFrom(ResponseExceptionMapper.class)) {
               register(o);
           }
       }

       // other
       this.builderDelegate.register(o, classes);
       return this;
   }

   @Override
   public RestClientBuilder register(Object o, Map<Class<?>, Integer> map) {


       if (o instanceof ResponseExceptionMapper) {

           //local
           ResponseExceptionMapper mapper = (ResponseExceptionMapper)o;
           HashMap<Class<?>, Integer> contracts = new HashMap<>();
           contracts.put(ResponseExceptionMapper.class, map.get(ResponseExceptionMapper.class));
           registerLocalProviderInstance(mapper, contracts);

           // other
           this.builderDelegate.register(o, map);

       } else {
           this.builderDelegate.register(o, map);
       }

       return this;
   }

   public Set<Object> getLocalProviderInstances() {
       return localProviderInstances;
   }

   public void registerLocalProviderInstance(Object provider, Map<Class<?>, Integer> contracts) {
       for (Object registered : getLocalProviderInstances()) {
           if (registered == provider) {
               LOG.infov("Provider already registered {0}", provider.getClass().getName());
               return;
           }
       }

       localProviderInstances.add(provider);
       configurationWrapper.registerLocalContract(provider.getClass(), contracts);
   }

   private final ResteasyClientBuilder builderDelegate;

   private final ConfigurationWrapper configurationWrapper;

   private final Config config;

   private URI baseURI;

   private Set<Object> localProviderInstances = new HashSet<Object>();
}
